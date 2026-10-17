package com.iptracker.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.iptracker.app.R;
import com.iptracker.app.firebase.FirebaseAuthManager;
import com.iptracker.app.ui.main.MainActivity;
import com.iptracker.app.data.AppDatabase;
import com.iptracker.app.data.entities.User;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private Button goToRegisterButton;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();
        setupListeners();
    }

    private void initializeComponents() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);
        progressBar = findViewById(R.id.progressBar);

        authManager = new FirebaseAuthManager();
        database = AppDatabase.getDatabase(this);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> performLogin());
        goToRegisterButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void performLogin() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        if (!validateInput(email, password)) {
            return;
        }

        loginButton.setEnabled(false);
        showProgressBar(true);

        authManager.signInWithEmail(email, password)
                .addOnSuccessListener(authResult -> {
                    showProgressBar(false);
                    saveUserToDatabase(authResult.getUser());
                    startMainActivity();
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    loginButton.setEnabled(true);

                    String errorMessage;
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = getString(R.string.error_invalid_credentials);
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        errorMessage = getString(R.string.error_account_not_exists);
                    } else {
                        errorMessage = getString(R.string.login_error, e.getMessage());
                    }

                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_required_field));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(getString(R.string.error_invalid_email));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_required_field));
            return false;
        }
        if (password.length() < 6) {
            passwordInput.setError(getString(R.string.error_invalid_password));
            return false;
        }
        return true;
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void saveUserToDatabase(FirebaseUser firebaseUser) {
        new Thread(() -> {
            try {
                User existingUser = database.userDao().getUserByEmail(firebaseUser.getEmail());
                if (existingUser == null) {
                    User newUser = new User(
                            firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "",
                            firebaseUser.getEmail(),
                            System.currentTimeMillis()
                    );
                    database.userDao().insert(newUser);
                } else {
                    existingUser.setLastLoginTimestamp(System.currentTimeMillis());
                    database.userDao().update(existingUser);
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this,
                        getString(R.string.db_error), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authManager.isUserSignedIn()) {
            startMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, GoogleLoginActivity.class));
        finish();
    }
}