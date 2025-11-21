package com.luxevista.resort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileInfoFragment extends Fragment {
    private TextInputEditText nameInput, emailInput, phoneInput;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        
        // Initialize views
        nameInput = view.findViewById(R.id.name_input);
        emailInput = view.findViewById(R.id.email_input);
        phoneInput = view.findViewById(R.id.phone_input);
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Set up save button
        view.findViewById(R.id.save_button).setOnClickListener(v -> saveProfile());
        
        // Load user data
        loadUserData();
        
        return view;
    }

    private void loadUserData() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            emailInput.setText(auth.getCurrentUser().getEmail());
            
            db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameInput.setText(documentSnapshot.getString("name"));
                        phoneInput.setText(documentSnapshot.getString("phone"));
                    }
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }
    
    private void saveProfile() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            
            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }
            
            db.collection("users").document(userId)
                .update("name", name, "phone", phone)
                .addOnSuccessListener(aVoid -> 
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> 
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
        }
    }
} 