package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class MusicActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "564509612023414f9beb9691c260fc67";
    private static final String CLIENT_SECRET = "9829d091cd004110b51b5cfbaae00153";
    private static final String TAG = "MusicActivity";
    
    private CardView happyCard, sadCard, relaxedCard, energeticCard;
    private TextView titleTextView, currentTrackText, currentArtistText;
    private ImageButton playPauseButton, previousButton, nextButton;
    private SeekBar progressSeekBar;
    private String accessToken = "";
    
    // Media Player
    private MediaPlayer mediaPlayer;
    private Handler progressHandler = new Handler();
    private boolean isPlaying = false;
    private String currentEmotion = "";
    private String[] currentPlaylist;
    private int currentTrackIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Initialize UI elements
        initializeViews();
        
        // Get Spotify access token
        getSpotifyAccessToken();

        // Set up emotion card click listeners
        setupEmotionCardListeners();
        
        // Set up media player controls
        setupMediaPlayerControls();
    }
    
    private void initializeViews() {
        titleTextView = findViewById(R.id.musicTitleTextView);
        happyCard = findViewById(R.id.happyCard);
        sadCard = findViewById(R.id.sadCard);
        relaxedCard = findViewById(R.id.relaxedCard);
        energeticCard = findViewById(R.id.energeticCard);
        
        // Media player controls
        playPauseButton = findViewById(R.id.playPauseButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        progressSeekBar = findViewById(R.id.progressSeekBar);
        currentTrackText = findViewById(R.id.currentTrackText);
        currentArtistText = findViewById(R.id.currentArtistText);
    }
    
    private void setupMediaPlayerControls() {
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousTrack();
            }
        });
        
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextTrack();
            }
        });
        
        progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupEmotionCardListeners() {
        happyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEmotion("happy", "😊 Happy");
                highlightSelectedEmotion(happyCard);
            }
        });

        sadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEmotion("sad", "😢 Sad");
                highlightSelectedEmotion(sadCard);
            }
        });

        relaxedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEmotion("chill", "😌 Relaxed");
                highlightSelectedEmotion(relaxedCard);
            }
        });

        energeticCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEmotion("energetic", "⚡ Energetic");
                highlightSelectedEmotion(energeticCard);
            }
        });
    }
    
    private void highlightSelectedEmotion(CardView selectedCard) {
        // Reset all cards
        resetCardHighlights();
        
        // Highlight selected card
        float dpValue = 16 * getResources().getDisplayMetrics().density;
        selectedCard.setCardElevation(dpValue);
        selectedCard.setScaleX(1.05f);
        selectedCard.setScaleY(1.05f);
    }
    
    private void resetCardHighlights() {
        CardView[] cards = {happyCard, sadCard, relaxedCard, energeticCard};
        float normalElevation = 8 * getResources().getDisplayMetrics().density;
        for (CardView card : cards) {
            card.setCardElevation(normalElevation);
            card.setScaleX(1.0f);
            card.setScaleY(1.0f);
        }
    }
    
    private void getSpotifyAccessToken() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://accounts.spotify.com/api/token");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    
                    String auth = CLIENT_ID + ":" + CLIENT_SECRET;
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                    connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
                    
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write("grant_type=client_credentials");
                    writer.flush();
                    writer.close();
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getString("access_token");
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error getting access token: " + e.getMessage());
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(String token) {
                if (token != null) {
                    accessToken = token;
                    Log.d(TAG, "Successfully obtained access token");
                } else {
                    Toast.makeText(MusicActivity.this, "Failed to connect to Spotify", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void selectEmotion(String emotion, String emotionName) {
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Connecting to Spotify...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentEmotion = emotionName;
        searchMusicForEmotion(emotion);
    }

    private void searchMusicForEmotion(String emotion) {
        new AsyncTask<String, Void, String[]>() {
            @Override
            protected String[] doInBackground(String... params) {
                try {
                    String query = getQueryForEmotion(params[0]);
                    String searchQuery = query.replace(" ", "%20");
                    URL url = new URL("https://api.spotify.com/v1/search?q=" + searchQuery + "&type=track&limit=10");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray tracks = jsonResponse.getJSONObject("tracks").getJSONArray("items");
                    
                    String[] playlist = new String[tracks.length()];
                    for (int i = 0; i < tracks.length(); i++) {
                        JSONObject track = tracks.getJSONObject(i);
                        String trackName = track.getString("name");
                        String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                        String previewUrl = track.optString("preview_url", "");
                        
                        playlist[i] = trackName + "|" + artistName + "|" + previewUrl;
                    }
                    
                    return playlist;
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error searching tracks: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(String[] playlist) {
                if (playlist != null && playlist.length > 0) {
                    currentPlaylist = playlist;
                    currentTrackIndex = 0;
                    playCurrentTrack();
                    Toast.makeText(MusicActivity.this, "Loaded " + currentEmotion + " playlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MusicActivity.this, "No tracks found for " + currentEmotion, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(emotion);
    }
    
    private String getQueryForEmotion(String emotion) {
        switch (emotion) {
            case "happy": return "happy upbeat pop dance";
            case "sad": return "sad melancholy emotional ballad";
            case "chill": return "chill relax ambient lofi";
            case "energetic": return "energetic workout rock electronic";
            default: return "pop music";
        }
    }
    
    private void playCurrentTrack() {
        if (currentPlaylist == null || currentTrackIndex >= currentPlaylist.length) {
            return;
        }
        
        String[] trackInfo = currentPlaylist[currentTrackIndex].split("\\|");
        String trackName = trackInfo[0];
        String artistName = trackInfo[1];
        String previewUrl = trackInfo[2];
        
        if (previewUrl.isEmpty()) {
            // Skip to next track if no preview available
            playNextTrack();
            return;
        }
        
        // Update UI
        currentTrackText.setText(trackName);
        currentArtistText.setText(artistName);
        
        // Play the track
        playTrackFromUrl(previewUrl);
    }
    
    private void playTrackFromUrl(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressSeekBar.setMax(mp.getDuration());
                    mp.start();
                    isPlaying = true;
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                    updateProgress();
                }
            });
            
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextTrack();
                }
            });
            
            mediaPlayer.prepareAsync();
            
        } catch (Exception e) {
            Log.e(TAG, "Error playing track: " + e.getMessage());
            Toast.makeText(this, "Error playing track", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.ic_play);
            isPlaying = false;
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
            updateProgress();
        }
    }
    
    private void playNextTrack() {
        if (currentPlaylist == null) return;
        
        currentTrackIndex = (currentTrackIndex + 1) % currentPlaylist.length;
        playCurrentTrack();
    }
    
    private void playPreviousTrack() {
        if (currentPlaylist == null) return;
        
        currentTrackIndex = (currentTrackIndex - 1 + currentPlaylist.length) % currentPlaylist.length;
        playCurrentTrack();
    }
    
    private void updateProgress() {
        if (mediaPlayer != null && isPlaying) {
            progressSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            progressHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateProgress();
                }
            }, 100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        progressHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 