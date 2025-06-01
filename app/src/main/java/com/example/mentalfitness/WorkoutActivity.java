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
    private TextView currentWeatherText, temperatureText, workoutLocationText;
    private Button generateWorkoutButton, refreshWeatherButton;
    private LinearLayout workoutListContainer;
    private CardView moodCard, fitnessCard, weatherCard;
    
    private String[] moods = {"Stressed", "Energetic", "Tired", "Motivated", "Anxious", "Happy"};
    private String[] fitnessLevels = {"Beginner", "Intermediate", "Advanced"};
    private String[] weatherConditions = {"Sunny", "Cloudy", "Rainy", "Snowy", "Partly Cloudy", "Windy"};
    private String[] weatherEmojis = {"☀️", "☁️", "🌧️", "❄️", "⛅", "💨"};
    private int[] temperatureRanges = {25, 15, 10, 5, 20, 18}; // Celsius
    
    private int selectedMoodIndex = 0;
    private int selectedFitnessIndex = 0;
    private int currentWeatherIndex = 0;
    
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        initializeViews();
        setupClickListeners();
        updateDisplays();
        generateWeatherData();
    }
    
    private void initializeViews() {
        currentMoodText = findViewById(R.id.currentMoodText);
        fitnessLevelText = findViewById(R.id.fitnessLevelText);
        recommendedWorkoutText = findViewById(R.id.recommendedWorkoutText);
        currentWeatherText = findViewById(R.id.currentWeatherText);
        temperatureText = findViewById(R.id.temperatureText);
        workoutLocationText = findViewById(R.id.workoutLocationText);
        generateWorkoutButton = findViewById(R.id.generateWorkoutButton);
        refreshWeatherButton = findViewById(R.id.refreshWeatherButton);
        workoutListContainer = findViewById(R.id.workoutListContainer);
        moodCard = findViewById(R.id.moodCard);
        fitnessCard = findViewById(R.id.fitnessCard);
        weatherCard = findViewById(R.id.weatherCard);
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
        
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleWeather();
            }
        });
        
        generateWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateWorkoutPlan();
            }
        });
        
        refreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateWeatherData();
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
    
    private void cycleWeather() {
        currentWeatherIndex = (currentWeatherIndex + 1) % weatherConditions.length;
        updateWeatherDisplay();
        updateWorkoutLocation();
    }
    
    private void generateWeatherData() {
        currentWeatherIndex = random.nextInt(weatherConditions.length);
        updateWeatherDisplay();
        updateWorkoutLocation();
        Toast.makeText(this, "Weather updated!", Toast.LENGTH_SHORT).show();
    }
    
    private void updateDisplays() {
        currentMoodText.setText("Current Mood: " + moods[selectedMoodIndex]);
        fitnessLevelText.setText("Fitness Level: " + fitnessLevels[selectedFitnessIndex]);
    }
    
    private void updateWeatherDisplay() {
        String weather = weatherConditions[currentWeatherIndex];
        String emoji = weatherEmojis[currentWeatherIndex];
        int baseTemp = temperatureRanges[currentWeatherIndex];
        int temperature = baseTemp + random.nextInt(10) - 5; // ±5 degrees variation
        
        currentWeatherText.setText(emoji + " " + weather);
        temperatureText.setText(temperature + "°C");
    }
    
    private void updateWorkoutLocation() {
        String weather = weatherConditions[currentWeatherIndex];
        String recommendedLocation = getRecommendedLocation(weather);
        workoutLocationText.setText("You should work out " + recommendedLocation);
    }
    
    private String getRecommendedLocation(String weather) {
        switch (weather) {
            case "Sunny":
                return "Outdoor";
            case "Partly Cloudy":
                return "Outdoor";
            case "Cloudy":
                return "Indoor or Outdoor";
            case "Rainy":
                return "Indoor";
            case "Snowy":
                return "Indoor";
            case "Windy":
                return "Indoor";
            default:
                return "Indoor";
        }
    }
    
    private void generateWorkoutPlan() {
        String mood = moods[selectedMoodIndex];
        String fitnessLevel = fitnessLevels[selectedFitnessIndex];
        String weather = weatherConditions[currentWeatherIndex];
        
        List<String> workouts = getWorkoutsForMoodFitnessAndWeather(mood, fitnessLevel, weather);
        
        workoutListContainer.removeAllViews();
        
        recommendedWorkoutText.setText("Weather-Based Workout Plan");
        
        for (int i = 0; i < workouts.size(); i++) {
            addWorkoutCard(workouts.get(i), i + 1);
        }
        
        Toast.makeText(this, "Generated workout plan for " + weather + " weather!", Toast.LENGTH_SHORT).show();
    }
    
    private List<String> getWorkoutsForMoodFitnessAndWeather(String mood, String fitnessLevel, String weather) {
        List<String> workouts = new ArrayList<>();
        
        // Base workouts by weather condition
        switch (weather) {
            case "Sunny":
                workouts.add("🌞 Outdoor Walking/Jogging - 25 minutes");
                workouts.add("🏃‍♂️ Park Circuit Training - 20 minutes");
                workouts.add("🧘‍♀️ Outdoor Yoga - 15 minutes");
                if (mood.equals("Energetic") || mood.equals("Motivated")) {
                    workouts.add("🚴‍♂️ Cycling or Hiking - 30 minutes");
                }
                break;
                
            case "Partly Cloudy":
                workouts.add("🚶‍♀️ Brisk Outdoor Walk - 20 minutes");
                workouts.add("🏋️‍♂️ Bodyweight Training Outside - 25 minutes");
                workouts.add("⛹️‍♂️ Sports Activities - 30 minutes");
                break;
                
            case "Cloudy":
                workouts.add("🏠 Indoor Cardio Workout - 20 minutes");
                workouts.add("💪 Strength Training - 25 minutes");
                workouts.add("🧘‍♂️ Indoor Yoga Flow - 15 minutes");
                if (mood.equals("Stressed") || mood.equals("Anxious")) {
                    workouts.add("🎵 Dance Workout - 20 minutes");
                }
                break;
                
            case "Rainy":
                workouts.add("🏠 Home HIIT Workout - 20 minutes");
                workouts.add("🧘‍♀️ Calming Indoor Yoga - 25 minutes");
                workouts.add("💪 Resistance Band Training - 15 minutes");
                if (mood.equals("Tired")) {
                    workouts.add("🤸‍♀️ Gentle Stretching Routine - 10 minutes");
                }
                break;
                
            case "Snowy":
                workouts.add("🏠 Warm-up Indoor Cardio - 15 minutes");
                workouts.add("🔥 High-Intensity Indoor Circuit - 25 minutes");
                workouts.add("🧘‍♂️ Hot Yoga Session - 30 minutes");
                break;
                
            case "Windy":
                workouts.add("🏠 Indoor Strength Training - 30 minutes");
                workouts.add("🤸‍♀️ Pilates Workout - 20 minutes");
                workouts.add("🧘‍♀️ Meditation & Light Stretching - 15 minutes");
                break;
        }
        
        // Adjust intensity based on fitness level
        if (fitnessLevel.equals("Beginner")) {
            workouts.add("😌 Cool Down & Relaxation - 5 minutes");
        } else if (fitnessLevel.equals("Advanced")) {
            workouts.add("💥 Bonus Challenge Exercise - 10 minutes");
        }
        
        // Add mood-specific recommendations
        switch (mood) {
            case "Stressed":
                workouts.add("🫁 Deep Breathing Exercise - 5 minutes");
                break;
            case "Anxious":
                workouts.add("🧘‍♂️ Mindfulness Practice - 10 minutes");
                break;
            case "Happy":
                workouts.add("🎉 Fun Dance Session - 15 minutes");
                break;
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