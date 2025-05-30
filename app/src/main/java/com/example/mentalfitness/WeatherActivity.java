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

public class WeatherActivity extends AppCompatActivity {

    private TextView currentWeatherText, temperatureText, moodSuggestionText;
    private Button refreshWeatherButton;
    private LinearLayout suggestionsContainer;
    private CardView weatherCard;
    
    private String[] weatherConditions = {"Sunny", "Cloudy", "Rainy", "Snowy", "Partly Cloudy", "Windy"};
    private String[] weatherEmojis = {"☀️", "☁️", "🌧️", "❄️", "⛅", "💨"};
    private int[] temperatureRanges = {25, 15, 10, 5, 20, 18}; // Celsius
    
    private Random random = new Random();
    private int currentWeatherIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initializeViews();
        setupClickListeners();
        generateWeatherData();
    }
    
    private void initializeViews() {
        currentWeatherText = findViewById(R.id.currentWeatherText);
        temperatureText = findViewById(R.id.temperatureText);
        moodSuggestionText = findViewById(R.id.moodSuggestionText);
        refreshWeatherButton = findViewById(R.id.refreshWeatherButton);
        suggestionsContainer = findViewById(R.id.suggestionsContainer);
        weatherCard = findViewById(R.id.weatherCard);
    }
    
    private void setupClickListeners() {
        refreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateWeatherData();
            }
        });
        
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleWeather();
            }
        });
    }
    
    private void cycleWeather() {
        currentWeatherIndex = (currentWeatherIndex + 1) % weatherConditions.length;
        updateWeatherDisplay();
        generateSuggestions();
    }
    
    private void generateWeatherData() {
        currentWeatherIndex = random.nextInt(weatherConditions.length);
        updateWeatherDisplay();
        generateSuggestions();
        Toast.makeText(this, "Weather data updated!", Toast.LENGTH_SHORT).show();
    }
    
    private void updateWeatherDisplay() {
        String weather = weatherConditions[currentWeatherIndex];
        String emoji = weatherEmojis[currentWeatherIndex];
        int baseTemp = temperatureRanges[currentWeatherIndex];
        int temperature = baseTemp + random.nextInt(10) - 5; // ±5 degrees variation
        
        currentWeatherText.setText(emoji + " " + weather);
        temperatureText.setText(temperature + "°C");
    }
    
    private void generateSuggestions() {
        String weather = weatherConditions[currentWeatherIndex];
        List<String> suggestions = getSuggestionsForWeather(weather);
        
        suggestionsContainer.removeAllViews();
        moodSuggestionText.setText("Suggestions for " + weather + " Weather");
        
        for (String suggestion : suggestions) {
            addSuggestionCard(suggestion);
        }
    }
    
    private List<String> getSuggestionsForWeather(String weather) {
        List<String> suggestions = new ArrayList<>();
        
        switch (weather) {
            case "Sunny":
                suggestions.add("🚶‍♀️ Take a mood-boosting walk in the sunshine");
                suggestions.add("🧘‍♂️ Practice outdoor meditation or yoga");
                suggestions.add("📚 Read a book in a sunny spot");
                suggestions.add("🌻 Spend time in nature or garden");
                suggestions.add("☕ Have your morning coffee outside");
                break;
                
            case "Cloudy":
                suggestions.add("🎨 Engage in creative indoor activities");
                suggestions.add("📖 Read or journal about your thoughts");
                suggestions.add("🍵 Enjoy a warm beverage and reflect");
                suggestions.add("🎵 Listen to calming music");
                suggestions.add("🧩 Work on puzzles or brain games");
                break;
                
            case "Rainy":
                suggestions.add("🛁 Take a relaxing warm bath");
                suggestions.add("🎬 Watch uplifting movies or shows");
                suggestions.add("🍲 Cook a comforting meal");
                suggestions.add("📞 Connect with friends via video call");
                suggestions.add("🎶 Practice mindfulness with rain sounds");
                break;
                
            case "Snowy":
                suggestions.add("❄️ Embrace the winter wonderland outside");
                suggestions.add("🔥 Create a cozy atmosphere indoors");
                suggestions.add("☕ Enjoy hot chocolate and warm foods");
                suggestions.add("🧶 Try calming activities like knitting");
                suggestions.add("📝 Practice gratitude journaling");
                break;
                
            case "Partly Cloudy":
                suggestions.add("🚴‍♂️ Go for a bike ride or light exercise");
                suggestions.add("🎯 Set and work on personal goals");
                suggestions.add("🌳 Take a nature walk with mindful breathing");
                suggestions.add("📱 Practice digital detox time");
                suggestions.add("🤝 Meet friends for outdoor activities");
                break;
                
            case "Windy":
                suggestions.add("🎈 Let go of stress - imagine wind carrying it away");
                suggestions.add("🏠 Create a calm indoor sanctuary");
                suggestions.add("🍃 Practice breathing exercises");
                suggestions.add("🎼 Listen to energizing music");
                suggestions.add("💭 Use time for introspection and planning");
                break;
        }
        
        return suggestions;
    }
    
    private void addSuggestionCard(String suggestion) {
        CardView suggestionCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 12, 0, 0);
        suggestionCard.setLayoutParams(cardParams);
        suggestionCard.setRadius(12);
        suggestionCard.setCardElevation(4);
        suggestionCard.setContentPadding(20, 16, 20, 16);
        suggestionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        TextView suggestionText = new TextView(this);
        suggestionText.setText(suggestion);
        suggestionText.setTextSize(15);
        suggestionText.setTextColor(getResources().getColor(R.color.colorText));
        suggestionText.setLineSpacing(4, 1.1f);
        
        suggestionCard.addView(suggestionText);
        suggestionsContainer.addView(suggestionCard);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 