package com.iptracker.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.iptracker.app.R;
import com.iptracker.app.PreferencesManager;
import java.util.Arrays;
import java.util.Date;

public class PreferencesActivity extends AppCompatActivity {
    private PreferencesManager preferencesManager;
    private SwitchMaterial themeSwitch;
    private Spinner languageSpinner;
    private Spinner styleSpinner;
    private Slider textSizeSlider;
    private TextView textSizeValue;
    private TextView lastModifiedText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        preferencesManager = new PreferencesManager(this);
        initializeViews();
        loadPreferences();
        setupListeners();
    }

    private void initializeViews() {
        themeSwitch = findViewById(R.id.switchTheme);
        languageSpinner = findViewById(R.id.languageSpinner);
        styleSpinner = findViewById(R.id.styleSpinner);
        textSizeSlider = findViewById(R.id.sliderTextSize);
        textSizeValue = findViewById(R.id.textSizeValue);
        lastModifiedText = findViewById(R.id.lastModifiedText);
        backButton = findViewById(R.id.buttonBack);

        setupSpinners();
        setupSlider();
    }

    private void setupSpinners() {
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Română", "English", "Français", "Español")
        );
        languageSpinner.setAdapter(languageAdapter);

        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("Modern", "Classic", "Minimalist", "Dark")
        );
        styleSpinner.setAdapter(styleAdapter);
    }

    private void setupSlider() {
        textSizeSlider.setValueFrom(0.5f);
        textSizeSlider.setValueTo(2.0f);
        textSizeSlider.setStepSize(0.1f);
    }

    private void loadPreferences() {
        themeSwitch.setChecked(preferencesManager.isDarkTheme());
        textSizeSlider.setValue(preferencesManager.getTextSize());
        updateTextSizeValue(preferencesManager.getTextSize());

        String currentLanguage = preferencesManager.getLanguage();
        int langPosition = getSpinnerPosition(languageSpinner, currentLanguage);
        if (langPosition >= 0) {
            languageSpinner.setSelection(langPosition);
        }

        int stylePosition = preferencesManager.getVisualStyle();
        if (stylePosition >= 0 && stylePosition < styleSpinner.getCount()) {
            styleSpinner.setSelection(stylePosition);
        }

        updateLastModifiedText(preferencesManager.getLastModified());
    }

    private void setupListeners() {
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setDarkTheme(isChecked);
            updateLastModifiedText(System.currentTimeMillis());
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                preferencesManager.setLanguage(selectedLanguage);
                updateLastModifiedText(System.currentTimeMillis());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        styleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferencesManager.setVisualStyle(position);
                updateLastModifiedText(System.currentTimeMillis());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        textSizeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                preferencesManager.setTextSize(value);
                updateTextSizeValue(value);
                updateLastModifiedText(System.currentTimeMillis());
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void updateTextSizeValue(float value) {
        textSizeValue.setText(getString(R.string.text_size_value_format, value));
    }

    private void updateLastModifiedText(long timestamp) {
        String formattedDate = new Date(timestamp).toString();
        lastModifiedText.setText(getString(R.string.last_modified, formattedDate));
    }

    private int getSpinnerPosition(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferencesManager.setLastModified(System.currentTimeMillis());
    }
}