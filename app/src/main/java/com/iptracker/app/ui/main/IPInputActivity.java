package com.iptracker.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iptracker.app.R;

public class IPInputActivity extends ComponentActivity {
    private TextInputLayout ipInputLayout;
    private TextInputEditText ipInputEditText;
    private Button submitButton;
    private Button backButton;
    private TextView errorTextView;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_input);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        ipInputLayout = findViewById(R.id.textInputLayout);
        ipInputEditText = findViewById(R.id.editTextIP);
        submitButton = findViewById(R.id.buttonSubmit);
        backButton = findViewById(R.id.buttonBack);
        errorTextView = findViewById(R.id.textViewError);
        titleTextView = findViewById(R.id.textViewTitle);

        errorTextView.setVisibility(View.GONE);
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> {
            String ip = ipInputEditText != null && ipInputEditText.getText() != null
                    ? ipInputEditText.getText().toString().trim()
                    : "";
            validateAndSubmitIP(ip);
        });

        backButton.setOnClickListener(v -> finish());

        if (ipInputEditText != null) {
            ipInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ipInputLayout.setError(null);
                    errorTextView.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void validateAndSubmitIP(String ip) {
        if (ip.isEmpty()) {
            showError(getString(R.string.error_empty_field));
        } else if (!isValidIPFormat(ip)) {
            showError(getString(R.string.error_invalid_ip));
        } else if (isPrivateIP(ip)) {

            ipInputLayout.setError(getString(R.string.error_private_ip));
        } else {
            Intent intent = new Intent(this, CountryInfoActivity.class);
            intent.putExtra("IP_ADDRESS", ip);
            startActivity(intent);
        }
    }

    private void showError(String message) {

        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        ipInputLayout.setError(message);
    }

    private boolean isValidIPFormat(String ip) {
        String regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(regex);
    }

    private boolean isPrivateIP(String ip) {
        try {
            String[] parts = ip.split("\\.");
            int firstOctet = Integer.parseInt(parts[0]);
            int secondOctet = Integer.parseInt(parts[1]);

            return (firstOctet == 10) ||
                    (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) ||
                    (firstOctet == 192 && secondOctet == 168) ||
                    (firstOctet == 127) ||
                    (firstOctet == 169 && secondOctet == 254);
        } catch (Exception e) {
            return false;
        }
    }
}