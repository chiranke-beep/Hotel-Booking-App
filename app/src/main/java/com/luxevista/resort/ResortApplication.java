package com.luxevista.resort;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ResortApplication extends Application {
    private static final String TAG = "ResortApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Initializing application");
        
        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this);
        
        try {
            // Initialize Firebase
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Log.d(TAG, "Firebase initialized successfully");
            }
            
            // Initialize Firebase App Check with Debug provider
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            try {
                firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                );
                Log.d(TAG, "Firebase App Check initialized successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error initializing Firebase App Check", e);
                // Continue without App Check in debug mode
            }
            
            // Enable Crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}