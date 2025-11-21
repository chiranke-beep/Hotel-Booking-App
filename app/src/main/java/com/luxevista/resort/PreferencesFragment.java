package com.luxevista.resort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PreferencesFragment extends Fragment {
    private SwitchMaterial notificationsSwitch;
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial locationSwitch;
    private SwitchMaterial autoLoginSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        // Initialize switches
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        locationSwitch = view.findViewById(R.id.location_switch);
        autoLoginSwitch = view.findViewById(R.id.auto_login_switch);

        // Load preferences
        loadPreferences();

        // Set up switch listeners
        setupSwitchListeners();

        return view;
    }

    private void loadPreferences() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    notificationsSwitch.setChecked(documentSnapshot.getBoolean("notificationsEnabled"));
                    darkModeSwitch.setChecked(documentSnapshot.getBoolean("darkModeEnabled"));
                    locationSwitch.setChecked(documentSnapshot.getBoolean("locationEnabled"));
                    autoLoginSwitch.setChecked(documentSnapshot.getBoolean("autoLoginEnabled"));
                }
            });
    }

    private void setupSwitchListeners() {
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updatePreference("notificationsEnabled", isChecked);
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updatePreference("darkModeEnabled", isChecked);
            // TODO: Implement dark mode
        });

        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updatePreference("locationEnabled", isChecked);
            // TODO: Handle location permissions
        });

        autoLoginSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updatePreference("autoLoginEnabled", isChecked);
        });
    }

    private void updatePreference(String key, boolean value) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .update(key, value)
            .addOnFailureListener(e -> {
                // Show error message
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), "Failed to update preference", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
    }
} 