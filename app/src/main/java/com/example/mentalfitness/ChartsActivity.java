package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ChartsActivity extends AppCompatActivity {

    private TextView weeklyOverviewText, moodTrendText, activitySummaryText;
    private LinearLayout moodChartContainer, activityChartContainer, statsContainer;
    private Button refreshDataButton;
    
    private SharedPreferences sharedPreferences;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private Random random = new Random();
    
    private String[] moodNames = {"Happy", "Sad", "Neutral", "Anxious", "Excited", "Tired", "Grateful"};
    private String[] moodEmojis = {"😊", "😢", "😐", "😰", "🤩", "😴", "🤗"};
    private int[] moodColors = {0xFF4CAF50, 0xFF2196F3, 0xFF9E9E9E, 0xFFFF9800, 0xFFE91E63, 0xFF673AB7, 0xFFFFEB3B};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        initializeViews();
        setupClickListeners();
        loadChartsData();
    }
    
    private void initializeViews() {
        weeklyOverviewText = findViewById(R.id.weeklyOverviewText);
        moodTrendText = findViewById(R.id.moodTrendText);
        activitySummaryText = findViewById(R.id.activitySummaryText);
        moodChartContainer = findViewById(R.id.moodChartContainer);
        activityChartContainer = findViewById(R.id.activityChartContainer);
        statsContainer = findViewById(R.id.statsContainer);
        refreshDataButton = findViewById(R.id.refreshDataButton);
        
        sharedPreferences = getSharedPreferences("fitness_data", Context.MODE_PRIVATE);
    }
    
    private void setupClickListeners() {
        refreshDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateSampleData();
                loadChartsData();
            }
        });
    }
    
    private void loadChartsData() {
        generateWeeklyOverview();
        generateMoodChart();
        generateActivityChart();
        generateStats();
    }
    
    private void generateWeeklyOverview() {
        Calendar cal = Calendar.getInstance();
        String currentWeek = "Week of " + dateFormat.format(cal.getTime());
        
        // Generate some sample metrics
        int avgMoodScore = 65 + random.nextInt(30); // 65-95
        int workoutsCompleted = 3 + random.nextInt(5); // 3-7
        int journalEntries = 2 + random.nextInt(6); // 2-7
        
        String overview = currentWeek + "\n\n" +
                "📈 Average Mood Score: " + avgMoodScore + "/100\n" +
                "💪 Workouts Completed: " + workoutsCompleted + "\n" +
                "📝 Journal Entries: " + journalEntries;
        
        weeklyOverviewText.setText(overview);
    }
    
    private void generateMoodChart() {
        moodChartContainer.removeAllViews();
        
        // Generate mood data for the past 7 days
        Calendar cal = Calendar.getInstance();
        Map<String, Integer> moodCounts = new HashMap<>();
        
        for (int i = 0; i < 7; i++) {
            String mood = moodNames[random.nextInt(moodNames.length)];
            moodCounts.put(mood, moodCounts.getOrDefault(mood, 0) + 1);
        }
        
        // Find most frequent mood
        String dominantMood = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantMood = entry.getKey();
            }
        }
        
        moodTrendText.setText("Dominant mood this week: " + dominantMood + " (" + maxCount + " days)");
        
        // Create visual representation
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            addMoodBar(entry.getKey(), entry.getValue(), 7);
        }
    }
    
    private void addMoodBar(String moodName, int count, int maxCount) {
        CardView moodCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 8, 0, 8);
        moodCard.setLayoutParams(cardParams);
        moodCard.setRadius(8);
        moodCard.setCardElevation(2);
        
        LinearLayout moodLayout = new LinearLayout(this);
        moodLayout.setOrientation(LinearLayout.HORIZONTAL);
        moodLayout.setPadding(16, 12, 16, 12);
        
        // Mood emoji and name
        TextView moodLabel = new TextView(this);
        String emoji = getMoodEmoji(moodName);
        moodLabel.setText(emoji + " " + moodName);
        moodLabel.setTextSize(14);
        moodLabel.setTextColor(getResources().getColor(R.color.colorText));
        
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        moodLabel.setLayoutParams(labelParams);
        
        // Progress bar
        View progressBar = new View(this);
        int barWidth = (int) (200 * ((float) count / maxCount));
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
            barWidth, 24
        );
        progressParams.setMargins(16, 0, 0, 0);
        progressBar.setLayoutParams(progressParams);
        progressBar.setBackgroundColor(getMoodColor(moodName));
        
        // Count text
        TextView countText = new TextView(this);
        countText.setText(count + "");
        countText.setTextSize(12);
        countText.setTextColor(getResources().getColor(R.color.colorText));
        countText.setPadding(16, 0, 0, 0);
        
        moodLayout.addView(moodLabel);
        moodLayout.addView(progressBar);
        moodLayout.addView(countText);
        moodCard.addView(moodLayout);
        moodChartContainer.addView(moodCard);
    }
    
    private void generateActivityChart() {
        activityChartContainer.removeAllViews();
        
        // Generate activity data
        String[] activities = {"Workouts", "Meditation", "Music Therapy", "Journaling", "Weather Activities"};
        int[] activityCounts = new int[activities.length];
        
        for (int i = 0; i < activities.length; i++) {
            activityCounts[i] = random.nextInt(8) + 1; // 1-8 activities
        }
        
        // Find most active area
        int maxActivityIndex = 0;
        for (int i = 1; i < activityCounts.length; i++) {
            if (activityCounts[i] > activityCounts[maxActivityIndex]) {
                maxActivityIndex = i;
            }
        }
        
        activitySummaryText.setText("Most active area: " + activities[maxActivityIndex] + 
                                   " (" + activityCounts[maxActivityIndex] + " times this week)");
        
        // Create activity bars
        for (int i = 0; i < activities.length; i++) {
            addActivityBar(activities[i], activityCounts[i], 8);
        }
    }
    
    private void addActivityBar(String activity, int count, int maxCount) {
        CardView activityCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 8, 0, 8);
        activityCard.setLayoutParams(cardParams);
        activityCard.setRadius(8);
        activityCard.setCardElevation(2);
        
        LinearLayout activityLayout = new LinearLayout(this);
        activityLayout.setOrientation(LinearLayout.HORIZONTAL);
        activityLayout.setPadding(16, 12, 16, 12);
        
        // Activity name
        TextView activityLabel = new TextView(this);
        activityLabel.setText(getActivityEmoji(activity) + " " + activity);
        activityLabel.setTextSize(14);
        activityLabel.setTextColor(getResources().getColor(R.color.colorText));
        
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        activityLabel.setLayoutParams(labelParams);
        
        // Progress bar
        View progressBar = new View(this);
        int barWidth = (int) (200 * ((float) count / maxCount));
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
            barWidth, 24
        );
        progressParams.setMargins(16, 0, 0, 0);
        progressBar.setLayoutParams(progressParams);
        progressBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        
        // Count text
        TextView countText = new TextView(this);
        countText.setText(count + "");
        countText.setTextSize(12);
        countText.setTextColor(getResources().getColor(R.color.colorText));
        countText.setPadding(16, 0, 0, 0);
        
        activityLayout.addView(activityLabel);
        activityLayout.addView(progressBar);
        activityLayout.addView(countText);
        activityCard.addView(activityLayout);
        activityChartContainer.addView(activityCard);
    }
    
    private void generateStats() {
        statsContainer.removeAllViews();
        
        // Generate various statistics
        String[] statLabels = {
            "Average Daily Score",
            "Consistency Streak",
            "Total App Usage",
            "Mood Improvement",
            "Goals Achieved"
        };
        
        String[] statValues = {
            (70 + random.nextInt(25)) + "/100",
            (3 + random.nextInt(15)) + " days",
            (45 + random.nextInt(30)) + " minutes",
            "+" + (5 + random.nextInt(20)) + "%",
            (2 + random.nextInt(8)) + "/10"
        };
        
        String[] statEmojis = {"📊", "🔥", "⏱️", "📈", "🎯"};
        
        for (int i = 0; i < statLabels.length; i++) {
            addStatCard(statEmojis[i], statLabels[i], statValues[i]);
        }
    }
    
    private void addStatCard(String emoji, String label, String value) {
        CardView statCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 8, 0, 8);
        statCard.setLayoutParams(cardParams);
        statCard.setRadius(12);
        statCard.setCardElevation(4);
        statCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        LinearLayout statLayout = new LinearLayout(this);
        statLayout.setOrientation(LinearLayout.HORIZONTAL);
        statLayout.setPadding(20, 16, 20, 16);
        statLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        TextView emojiText = new TextView(this);
        emojiText.setText(emoji);
        emojiText.setTextSize(24);
        emojiText.setPadding(0, 0, 16, 0);
        
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        ));
        
        TextView labelText = new TextView(this);
        labelText.setText(label);
        labelText.setTextSize(14);
        labelText.setTextColor(getResources().getColor(R.color.colorText));
        
        TextView valueText = new TextView(this);
        valueText.setText(value);
        valueText.setTextSize(18);
        valueText.setTypeface(null, android.graphics.Typeface.BOLD);
        valueText.setTextColor(getResources().getColor(R.color.colorPrimary));
        
        textLayout.addView(labelText);
        textLayout.addView(valueText);
        
        statLayout.addView(emojiText);
        statLayout.addView(textLayout);
        
        statCard.addView(statLayout);
        statsContainer.addView(statCard);
    }
    
    private String getMoodEmoji(String moodName) {
        for (int i = 0; i < moodNames.length; i++) {
            if (moodNames[i].equals(moodName)) {
                return moodEmojis[i];
            }
        }
        return "😐";
    }
    
    private int getMoodColor(String moodName) {
        for (int i = 0; i < moodNames.length; i++) {
            if (moodNames[i].equals(moodName)) {
                return moodColors[i];
            }
        }
        return 0xFF9E9E9E;
    }
    
    private String getActivityEmoji(String activity) {
        switch (activity) {
            case "Workouts": return "💪";
            case "Meditation": return "🧘‍♂️";
            case "Music Therapy": return "🎵";
            case "Journaling": return "📝";
            case "Weather Activities": return "🌤️";
            default: return "📊";
        }
    }
    
    private void generateSampleData() {
        // This would normally save some sample data to SharedPreferences
        // For demo purposes, we'll just refresh the display
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 