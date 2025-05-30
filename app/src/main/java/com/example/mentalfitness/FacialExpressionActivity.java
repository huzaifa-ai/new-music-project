package com.example.mentalfitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FacialExpressionActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "564509612023414f9beb9691c260fc67";
    private static final String CLIENT_SECRET = "9829d091cd004110b51b5cfbaae00153";
    private static final String TAG = "FacialExpressionActivity";
    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    
    private PreviewView cameraPreview;
    private TextView emotionStatusText, currentTrackText, currentArtistText, detectionStatusText;
    private ImageButton playPauseButton, nextButton, scanButton;
    private SeekBar progressSeekBar;
    
    private ProcessCameraProvider cameraProvider;
    private String accessToken = "";
    private MediaPlayer mediaPlayer;
    private Handler progressHandler = new Handler();
    private boolean isPlaying = false;
    private String[] currentPlaylist;
    private int currentTrackIndex = 0;
    private String currentDetectedEmotion = "";
    
    // Simple emotion detection simulation
    private String[] emotions = {"happy", "sad", "neutral", "angry", "surprised"};
    private String[] emotionLabels = {"😊 Happy", "😢 Sad", "😐 Neutral", "😠 Angry", "😮 Surprised"};
    private Random emotionSimulator = new Random();
    private boolean isDetecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_expression);

        initializeViews();
        getSpotifyAccessToken();
        setupControls();
        
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }
    
    private void initializeViews() {
        cameraPreview = findViewById(R.id.cameraPreview);
        emotionStatusText = findViewById(R.id.emotionStatusText);
        currentTrackText = findViewById(R.id.currentTrackText);
        currentArtistText = findViewById(R.id.currentArtistText);
        detectionStatusText = findViewById(R.id.detectionStatusText);
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        scanButton = findViewById(R.id.scanButton);
        progressSeekBar = findViewById(R.id.progressSeekBar);
    }
    
    private void setupControls() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDetecting) {
                    startEmotionDetection();
                } else {
                    stopEmotionDetection();
                }
            }
        });
        
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
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

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, 
                CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required for facial expression analysis", 
                             Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), new EmotionAnalyzer());

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Error binding camera", e);
        }
    }
    
    private void startEmotionDetection() {
        isDetecting = true;
        scanButton.setContentDescription("Stop Analysis");
        detectionStatusText.setText("🔍 Analyzing facial expressions...");
        detectionStatusText.setVisibility(View.VISIBLE);
        
        // Simulate emotion detection every 3 seconds
        Handler detectionHandler = new Handler();
        Runnable detectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (isDetecting) {
                    simulateEmotionDetection();
                    detectionHandler.postDelayed(this, 3000);
                }
            }
        };
        detectionHandler.postDelayed(detectionRunnable, 1000);
    }
    
    private void stopEmotionDetection() {
        isDetecting = false;
        scanButton.setContentDescription("Start Analysis");
        detectionStatusText.setText("Tap to start facial expression analysis");
    }
    
    private void simulateEmotionDetection() {
        int emotionIndex = emotionSimulator.nextInt(emotions.length);
        String detectedEmotion = emotions[emotionIndex];
        String emotionLabel = emotionLabels[emotionIndex];
        
        if (!detectedEmotion.equals(currentDetectedEmotion)) {
            currentDetectedEmotion = detectedEmotion;
            emotionStatusText.setText("Expression: " + emotionLabel);
            
            // Load music for detected emotion
            loadMusicForEmotion(detectedEmotion, emotionLabel);
        }
    }

    private class EmotionAnalyzer implements ImageAnalysis.Analyzer {
        @Override
        public void analyze(@NonNull ImageProxy image) {
            // This is where real emotion detection would happen
            // For now, we're using simulated detection triggered by user
            image.close();
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
                    Toast.makeText(FacialExpressionActivity.this, "Failed to connect to Spotify", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void loadMusicForEmotion(String emotion, String emotionLabel) {
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Connecting to Spotify...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AsyncTask<String, Void, String[]>() {
            @Override
            protected String[] doInBackground(String... params) {
                try {
                    String query = getQueryForEmotion(params[0]);
                    String searchQuery = query.replace(" ", "%20");
                    URL url = new URL("https://api.spotify.com/v1/search?q=" + searchQuery + "&type=track&limit=5");
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
                    Toast.makeText(FacialExpressionActivity.this, 
                                  "Playing music for " + emotionLabel, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FacialExpressionActivity.this, 
                                  "No tracks found for " + emotionLabel, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(emotion);
    }
    
    private String getQueryForEmotion(String emotion) {
        switch (emotion) {
            case "happy": return "happy upbeat pop energetic";
            case "sad": return "sad melancholy emotional slow";
            case "angry": return "rock metal intense aggressive";
            case "surprised": return "electronic dance exciting";
            case "neutral":
            default: return "chill relax ambient calm";
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
            playNextTrack();
            return;
        }
        
        currentTrackText.setText(trackName);
        currentArtistText.setText(artistName);
        
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
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        progressHandler.removeCallbacksAndMessages(null);
        isDetecting = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 