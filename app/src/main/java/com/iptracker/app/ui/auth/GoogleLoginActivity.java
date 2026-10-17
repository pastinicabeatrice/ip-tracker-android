package com.iptracker.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iptracker.app.R;
import com.iptracker.app.ui.main.MainActivity;
import com.iptracker.app.data.AppDatabase;
import com.iptracker.app.data.entities.User;
import com.iptracker.app.firebase.FirebaseAuthManager;

public class GoogleLoginActivity extends AppCompatActivity {
    private FirebaseAuthManager authManager;
    private GoogleSignInClient googleSignInClient;
    private AppDatabase database;
    private SignInButton signInButton;
    private Button loginEmailButton;
    private Button registerButton;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        initializeComponents();
        setupGoogleSignIn();
        setupClickListeners();
    }

    private void initializeComponents() {
        authManager = new FirebaseAuthManager();
        database = AppDatabase.getDatabase(this);
        signInButton = findViewById(R.id.sign_in_button);
        loginEmailButton = findViewById(R.id.loginEmailButton);
        registerButton = findViewById(R.id.registerButton);
        signInButton.setSize(SignInButton.SIZE_WIDE);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        authManager.initializeGoogleSignIn(this, getString(R.string.default_web_client_id));
    }

    private void setupClickListeners() {
        signInButton.setOnClickListener(v -> startSignIn());
        loginEmailButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        registerButton.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void startSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(this, getString(R.string.google_sign_in_error, e.getMessage()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulSignIn(task.getResult().getUser());
                    } else {
                        String errorMessage = getString(R.string.google_sign_in_error,
                                task.getException() != null ? task.getException().getMessage() : "Unknown error");
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSuccessfulSignIn(com.google.firebase.auth.FirebaseUser firebaseUser) {
        new Thread(() -> {
            try {
                User user = new User(firebaseUser.getDisplayName(), firebaseUser.getEmail(),
                        System.currentTimeMillis());
                User existingUser = database.userDao().getUserByEmail(user.getEmail());

                if (existingUser == null) {
                    long userId = database.userDao().insert(user);
                    user.setUserId(userId);
                } else {
                    existingUser.setLastLoginTimestamp(System.currentTimeMillis());
                    database.userDao().update(existingUser);
                }

                runOnUiThread(() -> {
                    Intent intent = new Intent(GoogleLoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(GoogleLoginActivity.this,
                        getString(R.string.db_error, e.getMessage()), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authManager.isUserSignedIn()) {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }
}