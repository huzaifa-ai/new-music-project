package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

public class WorkoutActivity extends AppCompatActivity {

    private EditText ageEditText;
    private Spinner genderSpinner;
    private EditText bodyFatEditText;
    private EditText vo2MaxEditText;
    private EditText moodEditText;
    private EditText intensityEditText;
    private Button recommendButton;
    private TextView recommendedWorkoutText;
    private CheckBox workoutDoneCheckbox;
    private CheckBox workoutSkippedCheckbox;
    private CardView homeBottomNav;
    private CardView settingsBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeViews();
        setupGenderSpinner();
        setupListeners();
    }

    private void initializeViews() {
        ageEditText = findViewById(R.id.ageEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        bodyFatEditText = findViewById(R.id.bodyFatEditText);
        vo2MaxEditText = findViewById(R.id.vo2MaxEditText);
        moodEditText = findViewById(R.id.moodEditText);
        intensityEditText = findViewById(R.id.intensityEditText);
        recommendButton = findViewById(R.id.recommendButton);
        recommendedWorkoutText = findViewById(R.id.recommendedWorkoutText);
        workoutDoneCheckbox = findViewById(R.id.workoutDoneCheckbox);
        workoutSkippedCheckbox = findViewById(R.id.workoutSkippedCheckbox);
        homeBottomNav = findViewById(R.id.homeBottomNav);
        settingsBottomNav = findViewById(R.id.settingsBottomNav);
    }

    private void setupGenderSpinner() {
        String[] genderOptions = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        recommendButton.setOnClickListener(v -> generateRecommendation());

        // Checkbox logic - only one can be selected
        workoutDoneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                workoutSkippedCheckbox.setChecked(false);
            }
        });

        workoutSkippedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                workoutDoneCheckbox.setChecked(false);
            }
        });

        // Bottom navigation
        homeBottomNav.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        settingsBottomNav.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void generateRecommendation() {
        // Validate inputs
        String age = ageEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String bodyFat = bodyFatEditText.getText().toString().trim();
        String vo2Max = vo2MaxEditText.getText().toString().trim();
        String mood = moodEditText.getText().toString().trim();
        String intensity = intensityEditText.getText().toString().trim();

        if (age.isEmpty() || gender.equals("Select Gender") || bodyFat.isEmpty() || 
            vo2Max.isEmpty() || mood.isEmpty() || intensity.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate recommendation based on inputs
        String recommendation = generateWorkoutRecommendation(age, gender, bodyFat, vo2Max, mood, intensity);
        
        recommendedWorkoutText.setText("Recommended workout: " + recommendation);
        recommendedWorkoutText.setVisibility(View.VISIBLE);
        
        // Reset checkboxes
        workoutDoneCheckbox.setChecked(false);
        workoutSkippedCheckbox.setChecked(false);

        Toast.makeText(this, "Workout recommendation generated!", Toast.LENGTH_SHORT).show();
    }

    private String generateWorkoutRecommendation(String age, String gender, String bodyFat, String vo2Max, String mood, String intensity) {
        try {
            int ageInt = Integer.parseInt(age);
            float bodyFatFloat = Float.parseFloat(bodyFat);
            float vo2MaxFloat = Float.parseFloat(vo2Max);
            float intensityFloat = Float.parseFloat(intensity);

            // Basic recommendation logic
            if (mood.toLowerCase().contains("stress") || mood.toLowerCase().contains("anxious")) {
                return "Yoga";
            } else if (mood.toLowerCase().contains("energy") || mood.toLowerCase().contains("motivated")) {
                if (intensityFloat >= 7) {
                    return "HIIT Training";
                } else {
                    return "Cardio Workout";
                }
            } else if (mood.toLowerCase().contains("tired") || mood.toLowerCase().contains("low")) {
                return "Light Stretching";
            } else if (vo2MaxFloat < 30) {
                return "Walking";
            } else if (vo2MaxFloat > 50) {
                return "Running";
            } else if (bodyFatFloat > 25 && gender.equals("Male")) {
                return "Fat Burning Cardio";
            } else if (bodyFatFloat > 32 && gender.equals("Female")) {
                return "Fat Burning Cardio";
            } else if (ageInt > 50) {
                return "Low Impact Strength Training";
            } else if (ageInt < 25) {
                return "High Intensity Training";
            } else {
                return "Balanced Workout";
            }
        } catch (NumberFormatException e) {
            return "General Fitness Workout";
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WorkoutActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
} 