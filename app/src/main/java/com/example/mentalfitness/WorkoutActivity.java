package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkoutActivity extends AppCompatActivity {

    private TextView currentMoodText, fitnessLevelText, recommendedWorkoutText;
    private Button generateWorkoutButton;
    private LinearLayout workoutListContainer;
    private CardView moodCard, fitnessCard;
    
    private String[] moods = {"Stressed", "Energetic", "Tired", "Motivated", "Anxious", "Happy"};
    private String[] fitnessLevels = {"Beginner", "Intermediate", "Advanced"};
    private int selectedMoodIndex = 0;
    private int selectedFitnessIndex = 0;
    
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeViews();
        setupClickListeners();
        updateDisplays();
    }
    
    private void initializeViews() {
        currentMoodText = findViewById(R.id.currentMoodText);
        fitnessLevelText = findViewById(R.id.fitnessLevelText);
        recommendedWorkoutText = findViewById(R.id.recommendedWorkoutText);
        generateWorkoutButton = findViewById(R.id.generateWorkoutButton);
        workoutListContainer = findViewById(R.id.workoutListContainer);
        moodCard = findViewById(R.id.moodCard);
        fitnessCard = findViewById(R.id.fitnessCard);
    }
    
    private void setupClickListeners() {
        moodCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleMood();
            }
        });
        
        fitnessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleFitnessLevel();
            }
        });
        
        generateWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateWorkoutPlan();
            }
        });
    }
    
    private void cycleMood() {
        selectedMoodIndex = (selectedMoodIndex + 1) % moods.length;
        updateDisplays();
    }
    
    private void cycleFitnessLevel() {
        selectedFitnessIndex = (selectedFitnessIndex + 1) % fitnessLevels.length;
        updateDisplays();
    }
    
    private void updateDisplays() {
        currentMoodText.setText("Current Mood: " + moods[selectedMoodIndex]);
        fitnessLevelText.setText("Fitness Level: " + fitnessLevels[selectedFitnessIndex]);
    }
    
    private void generateWorkoutPlan() {
        String mood = moods[selectedMoodIndex];
        String fitnessLevel = fitnessLevels[selectedFitnessIndex];
        
        List<String> workouts = getWorkoutsForMoodAndFitness(mood, fitnessLevel);
        
        workoutListContainer.removeAllViews();
        
        recommendedWorkoutText.setText("Recommended Workout Plan");
        
        for (int i = 0; i < workouts.size(); i++) {
            addWorkoutCard(workouts.get(i), i + 1);
        }
        
        Toast.makeText(this, "Generated workout plan for " + mood + " mood!", Toast.LENGTH_SHORT).show();
    }
    
    private List<String> getWorkoutsForMoodAndFitness(String mood, String fitnessLevel) {
        List<String> workouts = new ArrayList<>();
        
        // Base workouts by mood
        switch (mood) {
            case "Stressed":
                workouts.add("Deep Breathing Exercise - 5 minutes");
                workouts.add("Gentle Yoga Flow - 15 minutes");
                workouts.add("Light Walking - 20 minutes");
                break;
            case "Energetic":
                workouts.add("High-Intensity Interval Training - 20 minutes");
                workouts.add("Jump Rope - 10 minutes");
                workouts.add("Dance Workout - 15 minutes");
                break;
            case "Tired":
                workouts.add("Gentle Stretching - 10 minutes");
                workouts.add("Restorative Yoga - 20 minutes");
                workouts.add("Light Walking - 15 minutes");
                break;
            case "Motivated":
                workouts.add("Strength Training - 30 minutes");
                workouts.add("Cardio Circuit - 25 minutes");
                workouts.add("Core Workout - 15 minutes");
                break;
            case "Anxious":
                workouts.add("Mindful Breathing - 5 minutes");
                workouts.add("Tai Chi - 20 minutes");
                workouts.add("Swimming or Water Exercise - 25 minutes");
                break;
            case "Happy":
                workouts.add("Fun Dance Session - 20 minutes");
                workouts.add("Outdoor Activities - 30 minutes");
                workouts.add("Team Sports - 45 minutes");
                break;
        }
        
        // Adjust intensity based on fitness level
        if (fitnessLevel.equals("Beginner")) {
            workouts.add("Cool Down Stretches - 5 minutes");
        } else if (fitnessLevel.equals("Advanced")) {
            workouts.add("Bonus Challenge Exercise - 10 minutes");
        }
        
        return workouts;
    }
    
    private void addWorkoutCard(String workout, int number) {
        CardView workoutCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 16, 0, 0);
        workoutCard.setLayoutParams(cardParams);
        workoutCard.setRadius(12);
        workoutCard.setCardElevation(4);
        workoutCard.setContentPadding(20, 16, 20, 16);
        
        TextView workoutText = new TextView(this);
        workoutText.setText(number + ". " + workout);
        workoutText.setTextSize(16);
        workoutText.setTextColor(getResources().getColor(R.color.colorText));
        
        workoutCard.addView(workoutText);
        workoutListContainer.addView(workoutCard);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 