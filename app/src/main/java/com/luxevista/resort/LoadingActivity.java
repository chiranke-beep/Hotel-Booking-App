package com.luxevista.resort;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private static final int LOADING_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Navigate to LoginActivity after delay
        new Handler().postDelayed(() -> {
            startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
            finish();
        }, LOADING_DELAY);
    }
}