package com.luxevista.resort;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView welcomeImage;
    private TextView welcomeText;
    private TextView resortName;
    private TextView tagline;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Starting MainActivity");

        mAuth = FirebaseAuth.getInstance();
        
        // Sign out any existing user for testing
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "Signing out current user for testing");
            mAuth.signOut();
        }

        // Initialize views
        welcomeImage = findViewById(R.id.welcome_image);
        welcomeText = findViewById(R.id.welcome_text);
        resortName = findViewById(R.id.resort_name);
        tagline = findViewById(R.id.tagline);

        // Add a delay before navigating
        new Handler().postDelayed(() -> {
            Log.d(TAG, "Starting navigation after delay");
            Intent intent;
            if (mAuth.getCurrentUser() != null) {
                Log.d(TAG, "User is logged in, navigating to DashboardActivity");
                intent = new Intent(MainActivity.this, DashboardActivity.class);
            } else {
                Log.d(TAG, "User is not logged in, navigating to LoginActivity");
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 3000); // 3 second delay
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: MainActivity started");
    }
} 