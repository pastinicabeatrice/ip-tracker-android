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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.iptracker.app.R;
import com.iptracker.app.firebase.FirebaseAuthManager;
import com.iptracker.app.ui.main.MainActivity;
import com.iptracker.app.data.AppDatabase;
import com.iptracker.app.data.entities.User;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText nameInput;
    private Button registerButton;
    private Button goToLoginButton;
    private ProgressBar progressBar;
    private FirebaseAuthManager authManager;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
        setupListeners();
    }

    private void initializeComponents() {
        emailInput = findViewById(R.id.registerEmailInput);
        passwordInput = findViewById(R.id.registerPasswordInput);
        nameInput = findViewById(R.id.nameInput);
        registerButton = findViewById(R.id.registerButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);
        progressBar = findViewById(R.id.progressBar);

        authManager = new FirebaseAuthManager();
        database = AppDatabase.getDatabase(this);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> performRegistration());
        goToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void performRegistration() {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";

        if (!validateInput(email, password, name)) {
            return;
        }

        registerButton.setEnabled(false);
        showProgressBar(true);

        authManager.createAccount(email, password)
                .addOnSuccessListener(authResult -> {
                    showProgressBar(false);
                    saveUser(name, email);
                    startMainActivity();
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    registerButton.setEnabled(true);

                    String errorMessage;
                    if (e instanceof FirebaseAuthWeakPasswordException) {
                        errorMessage = getString(R.string.error_weak_password);
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = getString(R.string.error_invalid_email);
                    } else if (e instanceof FirebaseAuthUserCollisionException) {
                        errorMessage = getString(R.string.error_email_exists);
                    } else {
                        errorMessage = getString(R.string.register_error, e.getMessage());
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private boolean validateInput(String email, String password, String name) {
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.error_required_field));
            return false;
        }

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

    private void saveUser(String name, String email) {
        new Thread(() -> {
            try {
                User user = new User(name, email, System.currentTimeMillis());
                database.userDao().insert(user);
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