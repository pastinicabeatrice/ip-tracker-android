package com.iptracker.app.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.activity.ComponentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iptracker.app.R;
import com.iptracker.app.data.AppDatabase;
import com.iptracker.app.data.entities.IpSearch;
import com.iptracker.app.data.entities.User;
import com.iptracker.app.ui.adapters.IPHistoryAdapter;
import com.iptracker.app.ui.models.IPDetails;
import com.iptracker.app.ui.models.IPHistoryItem;
import com.iptracker.app.firebase.FirebaseAuthManager;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.textfield.TextInputEditText;

public class CountryInfoActivity extends ComponentActivity {
    private TextView ipTextView;
    private TextView infoTextView;
    private ProgressBar loadingIndicator;
    private ListView historyListView;
    private Button backButton;
    private TextInputEditText filterEditText;
    private IPHistoryAdapter adapter;
    private AppDatabase database;
    private FirebaseAuthManager authManager;
    private User currentUser;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<IPHistoryItem> ipHistoryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_info);

        initializeViews();
        setupDatabase();
        setupFirebase();
        processIncomingData();
        setupFilterListener();
    }

    private void initializeViews() {
        ipTextView = findViewById(R.id.textViewIP);
        infoTextView = findViewById(R.id.textViewCountryInfo);
        loadingIndicator = findViewById(R.id.progressBarLoading);
        historyListView = findViewById(R.id.listViewHistory);
        backButton = findViewById(R.id.buttonBack);
        filterEditText = findViewById(R.id.editTextFilter);

        backButton.setOnClickListener(v -> finish());
        adapter = new IPHistoryAdapter(this, ipHistoryItems);
        historyListView.setAdapter(adapter);
    }

    private void setupDatabase() {
        database = AppDatabase.getDatabase(this);
        executorService.execute(() -> {
            try {
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null) {
                    User user = database.userDao().getUserByEmail(fbUser.getEmail());
                    if (user == null) {
                        user = new User(fbUser.getDisplayName(), fbUser.getEmail(), System.currentTimeMillis());
                        long userId = database.userDao().insert(user);
                        user.setUserId(userId);
                    }
                    currentUser = user;
                    loadIpSearchHistory();
                }
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(this,
                        getString(R.string.db_error, e.getMessage()),
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    private void setupFirebase() {
        authManager = new FirebaseAuthManager();
        if (!authManager.isUserSignedIn()) {
            finish();
        }
    }

    private void setupFilterListener() {
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearchHistory(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterSearchHistory(String filter) {
        executorService.execute(() -> {
            try {
                List<IpSearch> filteredSearches = database.ipSearchDao()
                        .getSearchesByCountry("%" + filter + "%");
                List<IPHistoryItem> newItems = new ArrayList<>();

                for (IpSearch search : filteredSearches) {
                    newItems.add(new IPHistoryItem(
                            search.getIpAddress(),
                            search.getCountry() + ", " + search.getCity(),
                            search.getSearchTimestamp(),
                            search.isValid(),
                            calculateReliability(search)
                    ));
                }

                mainHandler.post(() -> {
                    ipHistoryItems.clear();
                    ipHistoryItems.addAll(newItems);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(this,
                        getString(R.string.db_error, e.getMessage()),
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    private void loadIpSearchHistory() {
        if (currentUser != null) {
            executorService.execute(() -> {
                try {
                    List<IpSearch> searches = database.ipSearchDao()
                            .getSearchesByUser(currentUser.getUserId());
                    List<IPHistoryItem> historyItems = new ArrayList<>();

                    for (IpSearch search : searches) {
                        historyItems.add(new IPHistoryItem(
                                search.getIpAddress(),
                                search.getCountry() + ", " + search.getCity(),
                                search.getSearchTimestamp(),
                                search.isValid(),
                                calculateReliability(search)
                        ));
                    }

                    mainHandler.post(() -> {
                        ipHistoryItems.clear();
                        ipHistoryItems.addAll(historyItems);
                        adapter.notifyDataSetChanged();
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> Toast.makeText(this,
                            getString(R.string.db_error, e.getMessage()),
                            Toast.LENGTH_LONG).show());
                }
            });
        }
    }

    private float calculateReliability(IpSearch search) {
        if (!search.isValid()) return 1.0f;
        if (search.getCity().isEmpty()) return 3.0f;
        return 5.0f;
    }

    private void processIncomingData() {
        IPDetails ipDetails;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ipDetails = getIntent().getParcelableExtra("IP_DETAILS", IPDetails.class);
        } else {
            ipDetails = getIntent().getParcelableExtra("IP_DETAILS");
        }

        String ip = ipDetails != null ? ipDetails.getIpAddress() :
                getIntent().getStringExtra("IP_ADDRESS");

        if (ip == null) {
            handleInvalidInput();
            return;
        }

        if (ipDetails != null) {
            displayIPDetails(ipDetails);
        } else if (validateIP(ip)) {
            setupInitialUI(ip);
            fetchIPInfo(ip);
        } else {
            handleInvalidInput();
        }
    }

    private void fetchIPInfo(String ip) {
        executorService.execute(() -> {
            try {
                URL url = new URL("https://ipapi.co/" + ip + "/json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.has("error")) {
                        mainHandler.post(() -> handleError(
                                new Exception(jsonResponse.optString("error", "Unknown error"))
                        ));
                        return;
                    }

                    IPDetails ipDetails = IPDetails.fromJson(jsonResponse);
                    mainHandler.post(() -> displayIPDetails(ipDetails));
                } else {
                    mainHandler.post(() -> handleError(
                            new Exception("Server returned code: " + responseCode)
                    ));
                }
            } catch (Exception e) {
                mainHandler.post(() -> handleError(e));
            }
        });
    }

    private void displayIPDetails(IPDetails ipDetails) {
        ipTextView.setText(getString(R.string.country_info_title, ipDetails.getIpAddress()));
        loadingIndicator.setVisibility(View.GONE);
        infoTextView.setText(ipDetails.getFormattedDetails());
        infoTextView.setVisibility(View.VISIBLE);
        saveIpSearch(ipDetails);
    }

    private void saveIpSearch(IPDetails ipDetails) {
        if (currentUser != null) {
            executorService.execute(() -> {
                try {
                    IpSearch ipSearch = new IpSearch(
                            currentUser.getUserId(),
                            ipDetails.getIpAddress(),
                            ipDetails.getCountry(),
                            ipDetails.getCity(),
                            System.currentTimeMillis(),
                            ipDetails.isValid()
                    );
                    database.ipSearchDao().insert(ipSearch);
                    loadIpSearchHistory();
                } catch (Exception e) {
                    mainHandler.post(() -> Toast.makeText(this,
                            getString(R.string.db_error, e.getMessage()),
                            Toast.LENGTH_LONG).show());
                }
            });
        }
    }

    private void handleInvalidInput() {
        loadingIndicator.setVisibility(View.GONE);
        infoTextView.setText(getString(R.string.error_invalid_ip));
        infoTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, getString(R.string.error_invalid_ip), Toast.LENGTH_LONG).show();
    }

    private void handleError(Exception error) {
        loadingIndicator.setVisibility(View.GONE);
        String errorMessage;

        if (error instanceof UnknownHostException) {
            errorMessage = getString(R.string.error_no_internet);
        } else if (error instanceof SocketTimeoutException) {
            errorMessage = getString(R.string.error_timeout);
        } else if (error instanceof IOException) {
            errorMessage = getString(R.string.error_network);
        } else {
            errorMessage = getString(R.string.error_fetching_data,
                    error.getMessage() != null ? error.getMessage() : "Unknown error");
        }

        infoTextView.setText(errorMessage);
        infoTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private boolean validateIP(String ip) {
        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(regex);
    }

    private void setupInitialUI(String ip) {
        ipTextView.setText(getString(R.string.country_info_title, ip));
        infoTextView.setText(getString(R.string.loading));
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}