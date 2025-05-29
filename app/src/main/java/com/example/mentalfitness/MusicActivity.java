package com.example.mentalfitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    private TextView titleTextView;
    private String accessToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Initialize UI elements
        titleTextView = findViewById(R.id.musicTitleTextView);
        happyCard = findViewById(R.id.happyCard);
        sadCard = findViewById(R.id.sadCard);
        relaxedCard = findViewById(R.id.relaxedCard);
        energeticCard = findViewById(R.id.energeticCard);

        // Get Spotify access token
        getSpotifyAccessToken();

        // Set up emotion card click listeners
        setupEmotionCardListeners();
    }

    private void setupEmotionCardListeners() {
        happyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAndPlayMusic("happy upbeat pop", "😊 Happy");
            }
        });

        sadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAndPlayMusic("sad melancholy indie", "😢 Sad");
            }
        });

        relaxedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAndPlayMusic("relaxing ambient chill", "😌 Relaxed");
            }
        });

        energeticCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAndPlayMusic("energetic workout rock", "⚡ Energetic");
            }
        });
    }

    private void getSpotifyAccessToken() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://accounts.spotify.com/api/token");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    
                    // Set up the connection
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    
                    // Create authorization header
                    String auth = CLIENT_ID + ":" + CLIENT_SECRET;
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                    connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
                    
                    // Send request body
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write("grant_type=client_credentials");
                    writer.flush();
                    writer.close();
                    
                    // Read response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parse JSON response
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

    private void searchAndPlayMusic(String query, String emotion) {
        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Connecting to Spotify...", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    String searchQuery = params[0].replace(" ", "%20");
                    URL url = new URL("https://api.spotify.com/v1/search?q=" + searchQuery + "&type=track&limit=1");
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
                    
                    // Parse response to get track URI
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray tracks = jsonResponse.getJSONObject("tracks").getJSONArray("items");
                    
                    if (tracks.length() > 0) {
                        JSONObject track = tracks.getJSONObject(0);
                        String trackName = track.getString("name");
                        String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                        String spotifyUri = track.getString("uri");
                        String externalUrl = track.getJSONObject("external_urls").getString("spotify");
                        
                        return trackName + "|" + artistName + "|" + spotifyUri + "|" + externalUrl;
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error searching tracks: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    String[] parts = result.split("\\|");
                    String trackName = parts[0];
                    String artistName = parts[1];
                    String spotifyUri = parts[2];
                    String externalUrl = parts[3];
                    
                    // Try to open in Spotify app, fallback to web player
                    openSpotifyTrack(spotifyUri, externalUrl, trackName, artistName, emotion);
                } else {
                    Toast.makeText(MusicActivity.this, "No tracks found for " + emotion, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(query);
    }

    private void openSpotifyTrack(String spotifyUri, String webUrl, String trackName, String artistName, String emotion) {
        try {
            // Try to open in Spotify app first
            Intent spotifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUri));
            spotifyIntent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + getPackageName()));
            startActivity(spotifyIntent);
            
            Toast.makeText(this, "Playing " + emotion + " music:\n" + trackName + " by " + artistName, Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            // Fallback to web player
            try {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                startActivity(webIntent);
                Toast.makeText(this, "Opening " + emotion + " music in browser:\n" + trackName + " by " + artistName, Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Unable to open Spotify. Please install the Spotify app.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 