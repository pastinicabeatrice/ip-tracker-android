package com.iptracker.app.ui.main;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.ComponentActivity;
import androidx.cardview.widget.CardView;
import java.util.Calendar;
import java.util.Date;
import android.os.Handler;
import android.os.Looper;

import com.iptracker.app.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncOperationsActivity extends ComponentActivity {
    private ProgressBar progressBar;
    private TextView progressText;
    private Button dateButton;
    private Button startButton;
    private Button backButton;
    private CardView statusCard;
    private TextView scheduledDateText;
    private TextView titleText;

    private Long selectedDate;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_operations);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.textViewProgress);
        dateButton = findViewById(R.id.buttonSelectDate);
        startButton = findViewById(R.id.buttonStart);
        backButton = findViewById(R.id.buttonBack);
        statusCard = findViewById(R.id.cardStatus);
        scheduledDateText = findViewById(R.id.textViewScheduledDate);
        titleText = findViewById(R.id.textViewTitle);

        progressBar.setMax(100);
        progressBar.setProgress(0);
        startButton.setEnabled(false);
        statusCard.setVisibility(View.GONE);
        progressText.setText(getString(R.string.async_progress, 0));
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
        dateButton.setOnClickListener(v -> showDatePicker());
        startButton.setOnClickListener(v -> startAsyncOperation());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    selectedDate = calendar.getTimeInMillis();
                    updateScheduledDateDisplay();
                    startButton.setEnabled(true);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateScheduledDateDisplay() {
        if (selectedDate != null) {
            statusCard.setVisibility(View.VISIBLE);
            String formattedDate = android.text.format.DateFormat
                    .getDateFormat(getApplicationContext())
                    .format(new Date(selectedDate));
            scheduledDateText.setText(getString(R.string.async_scheduled, formattedDate));
        }
    }

    private void startAsyncOperation() {
        setControlsEnabled(false);

        executorService.execute(() -> {
            try {
                if (selectedDate != null) {
                    long currentTime = System.currentTimeMillis();
                    if (selectedDate > currentTime) {
                        Thread.sleep(selectedDate - currentTime);
                    }

                    for (int i = 0; i <= 100; i += 10) {
                        final int progress = i;
                        mainHandler.post(() -> updateProgress(progress));
                        Thread.sleep(500);
                    }

                    mainHandler.post(() -> {
                        setControlsEnabled(true);
                        showCompletionMessage();
                    });
                }
            } catch (InterruptedException e) {
                mainHandler.post(() -> handleError(e));
            }
        });
    }

    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
        progressText.setText(getString(R.string.async_progress, progress));
    }

    private void setControlsEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
        dateButton.setEnabled(enabled);
    }

    private void showCompletionMessage() {
        Toast.makeText(
                this,
                getString(R.string.async_operation_complete),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void handleError(Exception error) {
        setControlsEnabled(true);
        Toast.makeText(
                this,
                getString(R.string.async_error, error.getMessage()),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}