package com.luxevista.resort;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Load the dashboard fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment dashboardFragment = new DashboardFragment();
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, dashboardFragment)
            .commit();
    }
} 