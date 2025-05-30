package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JournalingActivity extends AppCompatActivity {

    private EditText journalEntryEdit;
    private Button saveEntryButton, clearButton;
    private LinearLayout moodSelectorLayout, entriesContainer;
    private TextView selectedMoodText, currentDateText;
    
    private String[] moodEmojis = {"😊", "😢", "😐", "😠", "😰", "😴", "🤗", "😎"};
    private String[] moodLabels = {"Happy", "Sad", "Neutral", "Angry", "Anxious", "Tired", "Grateful", "Confident"};
    private int selectedMoodIndex = 0;
    
    private SharedPreferences sharedPreferences;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaling);

        initializeViews();
        setupClickListeners();
        setupMoodSelector();
        loadPreviousEntries();
        updateCurrentDate();
    }
    
    private void initializeViews() {
        journalEntryEdit = findViewById(R.id.journalEntryEdit);
        saveEntryButton = findViewById(R.id.saveEntryButton);
        clearButton = findViewById(R.id.clearButton);
        moodSelectorLayout = findViewById(R.id.moodSelectorLayout);
        entriesContainer = findViewById(R.id.entriesContainer);
        selectedMoodText = findViewById(R.id.selectedMoodText);
        currentDateText = findViewById(R.id.currentDateText);
        
        sharedPreferences = getSharedPreferences("journal_entries", Context.MODE_PRIVATE);
    }
    
    private void updateCurrentDate() {
        String currentDate = dateFormat.format(new Date());
        currentDateText.setText("Today: " + currentDate);
    }
    
    private void setupClickListeners() {
        saveEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournalEntry();
            }
        });
        
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCurrentEntry();
            }
        });
    }
    
    private void setupMoodSelector() {
        for (int i = 0; i < moodEmojis.length; i++) {
            final int moodIndex = i;
            
            CardView moodCard = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                120, 120
            );
            cardParams.setMargins(8, 0, 8, 0);
            moodCard.setLayoutParams(cardParams);
            moodCard.setRadius(60);
            moodCard.setCardElevation(4);
            moodCard.setContentPadding(0, 0, 0, 0);
            
            if (i == selectedMoodIndex) {
                moodCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else {
                moodCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            }
            
            TextView moodText = new TextView(this);
            moodText.setText(moodEmojis[i]);
            moodText.setTextSize(32);
            moodText.setGravity(android.view.Gravity.CENTER);
            moodText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ));
            
            moodCard.addView(moodText);
            moodCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMood(moodIndex);
                }
            });
            
            moodSelectorLayout.addView(moodCard);
        }
        
        updateSelectedMoodText();
    }
    
    private void selectMood(int moodIndex) {
        selectedMoodIndex = moodIndex;
        
        // Update mood selector visual state
        for (int i = 0; i < moodSelectorLayout.getChildCount(); i++) {
            CardView moodCard = (CardView) moodSelectorLayout.getChildAt(i);
            if (i == selectedMoodIndex) {
                moodCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else {
                moodCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            }
        }
        
        updateSelectedMoodText();
    }
    
    private void updateSelectedMoodText() {
        selectedMoodText.setText("Current mood: " + moodEmojis[selectedMoodIndex] + " " + moodLabels[selectedMoodIndex]);
    }
    
    private void saveJournalEntry() {
        String entryText = journalEntryEdit.getText().toString().trim();
        
        if (entryText.isEmpty()) {
            Toast.makeText(this, "Please write something before saving", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create entry with timestamp
        String timestamp = System.currentTimeMillis() + "";
        String dateTime = dateFormat.format(new Date()) + " at " + timeFormat.format(new Date());
        String mood = moodEmojis[selectedMoodIndex] + " " + moodLabels[selectedMoodIndex];
        String fullEntry = mood + "\n" + dateTime + "\n\n" + entryText;
        
        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("entry_" + timestamp, fullEntry);
        editor.apply();
        
        // Clear current entry
        journalEntryEdit.setText("");
        
        // Reload entries to show the new one
        loadPreviousEntries();
        
        Toast.makeText(this, "Journal entry saved!", Toast.LENGTH_SHORT).show();
    }
    
    private void clearCurrentEntry() {
        journalEntryEdit.setText("");
        Toast.makeText(this, "Entry cleared", Toast.LENGTH_SHORT).show();
    }
    
    private void loadPreviousEntries() {
        entriesContainer.removeAllViews();
        
        // Get all entries and sort by timestamp (newest first)
        java.util.Map<String, ?> allEntries = sharedPreferences.getAll();
        java.util.List<String> entryKeys = new java.util.ArrayList<>();
        
        for (String key : allEntries.keySet()) {
            if (key.startsWith("entry_")) {
                entryKeys.add(key);
            }
        }
        
        // Sort by timestamp (newest first)
        entryKeys.sort((a, b) -> {
            long timestampA = Long.parseLong(a.replace("entry_", ""));
            long timestampB = Long.parseLong(b.replace("entry_", ""));
            return Long.compare(timestampB, timestampA);
        });
        
        // Display entries
        for (String key : entryKeys) {
            String entryContent = sharedPreferences.getString(key, "");
            if (!entryContent.isEmpty()) {
                addEntryCard(entryContent, key);
            }
        }
        
        // Show message if no entries
        if (entryKeys.isEmpty()) {
            TextView noEntriesText = new TextView(this);
            noEntriesText.setText("📝 No journal entries yet. Start writing to track your thoughts and moods!");
            noEntriesText.setTextSize(14);
            noEntriesText.setTextColor(getResources().getColor(R.color.colorText));
            noEntriesText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            noEntriesText.setPadding(20, 40, 20, 40);
            entriesContainer.addView(noEntriesText);
        }
    }
    
    private void addEntryCard(String entryContent, final String entryKey) {
        CardView entryCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        entryCard.setLayoutParams(cardParams);
        entryCard.setRadius(12);
        entryCard.setCardElevation(4);
        entryCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        LinearLayout entryLayout = new LinearLayout(this);
        entryLayout.setOrientation(LinearLayout.VERTICAL);
        entryLayout.setPadding(20, 16, 20, 16);
        
        TextView entryText = new TextView(this);
        entryText.setText(entryContent);
        entryText.setTextSize(14);
        entryText.setTextColor(getResources().getColor(R.color.colorText));
        entryText.setLineSpacing(4, 1.2f);
        
        Button deleteButton = new Button(this);
        deleteButton.setText("🗑️ Delete");
        deleteButton.setTextSize(12);
        deleteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        deleteButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        deleteButton.setPadding(0, 8, 0, 0);
        
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry(entryKey);
            }
        });
        
        entryLayout.addView(entryText);
        entryLayout.addView(deleteButton);
        entryCard.addView(entryLayout);
        entriesContainer.addView(entryCard);
    }
    
    private void deleteEntry(String entryKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(entryKey);
        editor.apply();
        
        loadPreviousEntries();
        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 