package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AnimationUtils;

public class HomeActivity extends AppCompatActivity {

    private TextView caloriesTextView;
    private TextView stepsTextView;
    private CardView workoutCard;
    private CardView facialExpressionCard;
    private CardView weatherCard;
    private CardView journalingCard;
    private CardView chartsCard;
    private CardView assessmentCard;
    private CardView musicCard;
    private CardView homeBottomNav;
    private CardView settingsBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get username from intent if available
        String username = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username", "User");
        }

        // Initialize UI elements
        caloriesTextView = findViewById(R.id.caloriesTextView);
        stepsTextView = findViewById(R.id.stepsTextView);
        workoutCard = findViewById(R.id.workoutCard);
        facialExpressionCard = findViewById(R.id.facialExpressionCard);
        weatherCard = findViewById(R.id.weatherCard);
        journalingCard = findViewById(R.id.journalingCard);
        chartsCard = findViewById(R.id.chartsCard);
        assessmentCard = findViewById(R.id.assessmentCard);
        musicCard = findViewById(R.id.musicCard);
        homeBottomNav = findViewById(R.id.homeBottomNav);
        settingsBottomNav = findViewById(R.id.settingsBottomNav);

        // Set example data
        caloriesTextView.setText("216 kcal");
        stepsTextView.setText("4,381 steps");

        // Apply animations to cards
        animateCards();

        // Set up click listeners for all cards
        setupCardClickListeners();
    }

    private void animateCards() {
        // Apply a staggered animation to the cards
        CardView[] cards = {workoutCard, facialExpressionCard, weatherCard, journalingCard, 
                           chartsCard, assessmentCard, musicCard};
        
        for (int i = 0; i < cards.length; i++) {
            CardView card = cards[i];
            card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down));
        }
    }

    private void setupCardClickListeners() {
        // Workout Recommendation card
        workoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WorkoutActivity.class);
                startActivity(intent);
            }
        });
        
        // Facial Expression Analysis card
        facialExpressionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FacialExpressionActivity.class);
                startActivity(intent);
            }
        });
        
        // Weather-Based Suggestions card
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });
        
        // Journaling card
        journalingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, JournalingActivity.class);
                startActivity(intent);
            }
        });
        
        // Charts card
        chartsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChartsActivity.class);
                startActivity(intent);
            }
        });
        
        // Assessment card
        assessmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AssessmentActivity.class);
                startActivity(intent);
            }
        });
        
        // Music card - launches MusicActivity
        musicCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MusicActivity.class);
                startActivity(intent);
            }
        });

        // Bottom navigation
        homeBottomNav.setOnClickListener(v -> {
            // Already on home, show toast
            Toast.makeText(this, "You are already on Home", Toast.LENGTH_SHORT).show();
        });

        settingsBottomNav.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    @Override
    public void onBackPressed() {
        // Return to login screen with animation
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}