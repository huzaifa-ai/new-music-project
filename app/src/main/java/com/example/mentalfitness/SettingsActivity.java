package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;
    private Switch workoutRemindersSwitch;
    private Switch assessmentRemindersSwitch;
    private Button privacyPolicyButton;
    private Button termsOfServiceButton;
    private Button clearDataButton;
    private Button logoutButton;
    private CardView homeBottomNav;
    private CardView settingsBottomNav;
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MentalFitnessPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        loadSavedPreferences();
        setupClickListeners();
    }

    private void initializeViews() {
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        workoutRemindersSwitch = findViewById(R.id.workoutRemindersSwitch);
        assessmentRemindersSwitch = findViewById(R.id.assessmentRemindersSwitch);
        privacyPolicyButton = findViewById(R.id.privacyPolicyButton);
        termsOfServiceButton = findViewById(R.id.termsOfServiceButton);
        clearDataButton = findViewById(R.id.clearDataButton);
        logoutButton = findViewById(R.id.logoutButton);
        homeBottomNav = findViewById(R.id.homeBottomNav);
        settingsBottomNav = findViewById(R.id.settingsBottomNav);
    }

    private void loadSavedPreferences() {
        // Load saved preferences
        darkModeSwitch.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        workoutRemindersSwitch.setChecked(sharedPreferences.getBoolean("workout_reminders", true));
        assessmentRemindersSwitch.setChecked(sharedPreferences.getBoolean("assessment_reminders", true));
    }

    private void setupClickListeners() {
        // Dark mode switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ? "Dark mode enabled" : "Dark mode disabled", 
                          Toast.LENGTH_SHORT).show();
        });

        // Workout reminders switch
        workoutRemindersSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("workout_reminders", isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ? "Workout reminders enabled" : "Workout reminders disabled", 
                          Toast.LENGTH_SHORT).show();
        });

        // Assessment reminders switch
        assessmentRemindersSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("assessment_reminders", isChecked);
            editor.apply();
            Toast.makeText(this, isChecked ? "Assessment reminders enabled" : "Assessment reminders disabled", 
                          Toast.LENGTH_SHORT).show();
        });

        // Privacy Policy button
        privacyPolicyButton.setOnClickListener(v -> {
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
            // Here you would typically open a web view or dialog with privacy policy
        });

        // Terms of Service button
        termsOfServiceButton.setOnClickListener(v -> {
            Toast.makeText(this, "Terms of Service clicked", Toast.LENGTH_SHORT).show();
            // Here you would typically open a web view or dialog with terms of service
        });

        // Clear all data button
        clearDataButton.setOnClickListener(v -> {
            // Clear all stored data
            editor.clear();
            editor.apply();
            
            // Reset switches to default values
            darkModeSwitch.setChecked(false);
            workoutRemindersSwitch.setChecked(true);
            assessmentRemindersSwitch.setChecked(true);
            
            Toast.makeText(this, "All data cleared successfully", Toast.LENGTH_LONG).show();
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();
            
            // Clear any stored user data
            editor.clear();
            editor.apply();
            
            // Navigate back to login screen
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

        // Bottom navigation
        homeBottomNav.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        settingsBottomNav.setOnClickListener(v -> {
            // Already in settings, do nothing or show a toast
            Toast.makeText(this, "You are already in Settings", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        // Go back to home activity
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
} 