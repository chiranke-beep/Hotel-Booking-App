package com.luxevista.resort;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AttractionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_list);

        // Load the attraction fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment attractionFragment = new AttractionFragment();
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, attractionFragment)
            .commit();
    }
} 