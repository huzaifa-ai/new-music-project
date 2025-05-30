package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssessmentActivity extends AppCompatActivity {

    private LinearLayout questionsContainer;
    private Button submitAssessmentButton, startNewAssessmentButton;
    private TextView assessmentTitleText, resultText, lastAssessmentText;
    private CardView resultCard;
    
    private SharedPreferences sharedPreferences;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    
    private String[] questions = {
        "How often have you felt down, depressed, or hopeless in the past 2 weeks?",
        "How often have you felt nervous, anxious, or on edge in the past 2 weeks?",
        "How well have you been sleeping lately?",
        "How satisfied are you with your energy levels?",
        "How connected do you feel to family and friends?",
        "How confident do you feel about handling daily activities?",
        "How often do you engage in activities you enjoy?",
        "How well are you managing stress in your life?",
        "How positive do you feel about your future?",
        "How satisfied are you with your current life situation?"
    };
    
    private String[] answerOptions = {
        "Not at all", "Several days", "More than half the days", "Nearly every day"
    };
    
    private int[] answerScores = {0, 1, 2, 3}; // Higher scores indicate more concern
    private RadioGroup[] questionGroups;
    private boolean assessmentInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        initializeViews();
        setupClickListeners();
        loadLastAssessment();
        showAssessmentIntro();
    }
    
    private void initializeViews() {
        questionsContainer = findViewById(R.id.questionsContainer);
        submitAssessmentButton = findViewById(R.id.submitAssessmentButton);
        startNewAssessmentButton = findViewById(R.id.startNewAssessmentButton);
        assessmentTitleText = findViewById(R.id.assessmentTitleText);
        resultText = findViewById(R.id.resultText);
        lastAssessmentText = findViewById(R.id.lastAssessmentText);
        resultCard = findViewById(R.id.resultCard);
        
        sharedPreferences = getSharedPreferences("assessments", Context.MODE_PRIVATE);
        questionGroups = new RadioGroup[questions.length];
    }
    
    private void setupClickListeners() {
        startNewAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAssessment();
            }
        });
        
        submitAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAssessment();
            }
        });
    }
    
    private void showAssessmentIntro() {
        assessmentTitleText.setText("Mental Wellness Assessment");
        
        TextView introText = new TextView(this);
        introText.setText("📋 This assessment will help you understand your current mental wellness state.\n\n" +
                "• Takes about 3-5 minutes to complete\n" +
                "• Your responses are private and stored locally\n" +
                "• Results provide personalized recommendations\n\n" +
                "Ready to begin?");
        introText.setTextSize(14);
        introText.setTextColor(getResources().getColor(R.color.colorText));
        introText.setLineSpacing(6, 1.2f);
        introText.setPadding(20, 20, 20, 20);
        
        questionsContainer.removeAllViews();
        questionsContainer.addView(introText);
        
        startNewAssessmentButton.setVisibility(View.VISIBLE);
        submitAssessmentButton.setVisibility(View.GONE);
    }
    
    private void startAssessment() {
        assessmentInProgress = true;
        assessmentTitleText.setText("Mental Wellness Assessment");
        
        questionsContainer.removeAllViews();
        
        for (int i = 0; i < questions.length; i++) {
            addQuestionCard(i, questions[i]);
        }
        
        startNewAssessmentButton.setVisibility(View.GONE);
        submitAssessmentButton.setVisibility(View.VISIBLE);
        resultCard.setVisibility(View.GONE);
    }
    
    private void addQuestionCard(int questionIndex, String question) {
        CardView questionCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        questionCard.setLayoutParams(cardParams);
        questionCard.setRadius(12);
        questionCard.setCardElevation(4);
        questionCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        LinearLayout questionLayout = new LinearLayout(this);
        questionLayout.setOrientation(LinearLayout.VERTICAL);
        questionLayout.setPadding(20, 16, 20, 16);
        
        TextView questionText = new TextView(this);
        questionText.setText((questionIndex + 1) + ". " + question);
        questionText.setTextSize(14);
        questionText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        questionText.setTypeface(null, android.graphics.Typeface.BOLD);
        questionText.setPadding(0, 0, 0, 12);
        
        RadioGroup answerGroup = new RadioGroup(this);
        answerGroup.setOrientation(RadioGroup.VERTICAL);
        questionGroups[questionIndex] = answerGroup;
        
        for (int j = 0; j < answerOptions.length; j++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(answerOptions[j]);
            radioButton.setTextSize(13);
            radioButton.setTextColor(getResources().getColor(R.color.colorText));
            radioButton.setPadding(0, 8, 0, 8);
            radioButton.setId(j);
            
            answerGroup.addView(radioButton);
        }
        
        questionLayout.addView(questionText);
        questionLayout.addView(answerGroup);
        questionCard.addView(questionLayout);
        questionsContainer.addView(questionCard);
    }
    
    private void submitAssessment() {
        // Check if all questions are answered
        for (int i = 0; i < questionGroups.length; i++) {
            if (questionGroups[i].getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all questions before submitting", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Calculate score
        int totalScore = 0;
        for (RadioGroup group : questionGroups) {
            int selectedId = group.getCheckedRadioButtonId();
            totalScore += answerScores[selectedId];
        }
        
        // Save assessment
        String timestamp = System.currentTimeMillis() + "";
        String dateTime = dateFormat.format(new Date());
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_score", totalScore);
        editor.putString("last_date", dateTime);
        editor.putString("last_timestamp", timestamp);
        editor.apply();
        
        // Show results
        showResults(totalScore, dateTime);
        
        assessmentInProgress = false;
        Toast.makeText(this, "Assessment completed and saved!", Toast.LENGTH_SHORT).show();
    }
    
    private void showResults(int score, String date) {
        questionsContainer.removeAllViews();
        
        assessmentTitleText.setText("Assessment Results");
        
        String scoreCategory = getScoreCategory(score);
        String recommendations = getRecommendations(score);
        
        String resultsText = "📊 Your Score: " + score + "/" + (questions.length * 3) + "\n\n" +
                "📈 Status: " + scoreCategory + "\n\n" +
                "💡 Recommendations:\n" + recommendations + "\n\n" +
                "📅 Assessment Date: " + date;
        
        resultText.setText(resultsText);
        resultCard.setVisibility(View.VISIBLE);
        
        startNewAssessmentButton.setVisibility(View.VISIBLE);
        startNewAssessmentButton.setText("Take Another Assessment");
        submitAssessmentButton.setVisibility(View.GONE);
    }
    
    private String getScoreCategory(int score) {
        int maxScore = questions.length * 3;
        double percentage = (double) score / maxScore;
        
        if (percentage <= 0.25) {
            return "Excellent Mental Wellness 🌟";
        } else if (percentage <= 0.50) {
            return "Good Mental Wellness 😊";
        } else if (percentage <= 0.75) {
            return "Moderate Concerns 😐";
        } else {
            return "Needs Attention 😔";
        }
    }
    
    private String getRecommendations(int score) {
        int maxScore = questions.length * 3;
        double percentage = (double) score / maxScore;
        
        if (percentage <= 0.25) {
            return "• Keep up your great mental health practices!\n" +
                   "• Continue regular exercise and social connections\n" +
                   "• Maintain your current stress management techniques\n" +
                   "• Consider helping others as it boosts wellbeing";
        } else if (percentage <= 0.50) {
            return "• Focus on consistent self-care routines\n" +
                   "• Use the app's meditation and music features\n" +
                   "• Ensure adequate sleep and regular exercise\n" +
                   "• Keep journaling your thoughts and feelings";
        } else if (percentage <= 0.75) {
            return "• Consider talking to a counselor or therapist\n" +
                   "• Use relaxation techniques daily\n" +
                   "• Prioritize sleep hygiene and regular meals\n" +
                   "• Reach out to supportive friends and family\n" +
                   "• Limit stressful activities when possible";
        } else {
            return "• Strongly consider professional mental health support\n" +
                   "• Practice stress reduction techniques\n" +
                   "• Ensure you're getting enough sleep and nutrition\n" +
                   "• Connect with trusted friends, family, or support groups\n" +
                   "• Take assessment results to a healthcare provider";
        }
    }
    
    private void loadLastAssessment() {
        String lastDate = sharedPreferences.getString("last_date", "");
        int lastScore = sharedPreferences.getInt("last_score", -1);
        
        if (!lastDate.isEmpty() && lastScore >= 0) {
            String scoreCategory = getScoreCategory(lastScore);
            lastAssessmentText.setText("Last assessment: " + lastDate + "\nScore: " + lastScore + " - " + scoreCategory);
            lastAssessmentText.setVisibility(View.VISIBLE);
        } else {
            lastAssessmentText.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (assessmentInProgress) {
            Toast.makeText(this, "Please complete or cancel the current assessment", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
        finish();
    }
} 