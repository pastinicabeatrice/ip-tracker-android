package com.iptracker.app.ui.main;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.iptracker.app.R;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JsonProcessingActivity extends AppCompatActivity {
    private TextInputLayout processingTypeLayout;
    private AutoCompleteTextView processingTypeDropdown;
    private CheckBox autoProcessCheckbox;
    private SeekBar complexitySeekBar;
    private TextView ratingText;
    private TextView ratingValueText;
    private Button processButton;
    private Button backButton;
    private TextView resultText;

    private final List<String> processingOptions = Arrays.asList("Basic", "Detailed", "Advanced");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_processing);

        if (!initializeViews()) {
            Toast.makeText(this, "Eroare la inițializarea interfeței", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupProcessingTypeDropdown();
        setupListeners();
    }

    private boolean initializeViews() {
        try {
            processingTypeLayout = findViewById(R.id.processingTypeLayout);
            processingTypeDropdown = findViewById(R.id.processingTypeDropdown);
            autoProcessCheckbox = findViewById(R.id.autoProcessCheckbox);
            complexitySeekBar = findViewById(R.id.complexitySeekBar);
            ratingText = findViewById(R.id.ratingText);
            ratingValueText = findViewById(R.id.ratingValueText);
            processButton = findViewById(R.id.processButton);
            backButton = findViewById(R.id.buttonBack);
            resultText = findViewById(R.id.resultText);

            if (processingTypeLayout == null || processingTypeDropdown == null ||
                    autoProcessCheckbox == null || complexitySeekBar == null ||
                    ratingText == null || ratingValueText == null ||
                    processButton == null || backButton == null || resultText == null) {
                return false;
            }

            complexitySeekBar.setMax(5);
            complexitySeekBar.setProgress(3);
            updateRatingText(3);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setupProcessingTypeDropdown() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, processingOptions);
            processingTypeDropdown.setAdapter(adapter);
            processingTypeDropdown.setText(processingOptions.get(0), false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la setarea opțiunilor", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        try {
            backButton.setOnClickListener(v -> finish());

            processButton.setOnClickListener(v -> {
                String result = processJsonData();
                resultText.setText(result);
                if (result.startsWith("Eroare")) {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                }
            });

            complexitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateRatingText(progress);
                    if (autoProcessCheckbox.isChecked()) {
                        resultText.setText(processJsonData());
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            autoProcessCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    resultText.setText(processJsonData());
                }
            });

            processingTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
                if (autoProcessCheckbox.isChecked()) {
                    resultText.setText(processJsonData());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la setarea listeners", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRatingText(int progress) {
        try {
            ratingValueText.setText(getString(R.string.rating_format, progress));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String processJsonData() {
        try {
            String selectedType = processingTypeDropdown.getText().toString();
            int complexity = complexitySeekBar.getProgress();

            JSONObject jsonData = new JSONObject();
            jsonData.put("type", selectedType);
            jsonData.put("complexity", complexity);
            jsonData.put("autoProcess", autoProcessCheckbox.isChecked());
            jsonData.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date()));

            switch (selectedType) {
                case "Basic":
                    jsonData.put("description", "Procesare de bază a datelor JSON");
                    break;
                case "Detailed":
                    jsonData.put("description", "Procesare detaliată cu validări suplimentare");
                    JSONArray validations = new JSONArray();
                    validations.put("Format");
                    validations.put("Structură");
                    validations.put("Conținut");
                    jsonData.put("validations", validations);
                    break;
                case "Advanced":
                    jsonData.put("description", "Procesare avansată cu analiză completă");
                    JSONObject analytics = new JSONObject();
                    analytics.put("performValidations", true);
                    analytics.put("deepScan", true);
                    analytics.put("generateReport", true);
                    jsonData.put("analytics", analytics);
                    break;
            }

            StringBuilder result = new StringBuilder();
            result.append("Rezultat Procesare JSON:\n\n");
            result.append(jsonData.toString(2));

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Eroare la procesarea JSON: " + e.getMessage();
        }
    }
}