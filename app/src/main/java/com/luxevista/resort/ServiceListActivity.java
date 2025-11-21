package com.luxevista.resort;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.luxevista.resort.fragments.ServiceFragment;

public class ServiceListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        // Load the service fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment serviceFragment = new ServiceFragment();
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, serviceFragment)
            .commit();
    }
} 