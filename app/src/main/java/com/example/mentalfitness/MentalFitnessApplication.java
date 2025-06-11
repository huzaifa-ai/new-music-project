package com.example.mentalfitness;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MentalFitnessApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase with explicit configuration
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyBQAqDsm0p7eL_mmnFhIZ92kpDIpFWCRTU")
                    .setApplicationId("1:170592380310:android:bdb9be49093a9b3fb729b7")
                    .setProjectId("fitness-40f23")
                    .setStorageBucket("fitness-40f23.firebasestorage.app")
                    .build();
            
            FirebaseApp.initializeApp(this, options);
        }
    }
} 