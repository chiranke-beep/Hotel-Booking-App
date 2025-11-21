package com.luxevista.resort;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luxevista.resort.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // User is not logged in, redirect to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupProfileInfo();
        setupSaveButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> {
                auth.signOut();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void setupProfileInfo() {
        String userId = currentUser.getUid();
        String currentEmail = currentUser.getEmail();
        
        // Set email from Firebase Auth
        binding.emailInput.setText(currentEmail);
        
        // Load user data from Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        
                        if (name != null && !name.isEmpty()) {
                            binding.nameInput.setText(name);
                        }
                        if (phone != null && !phone.isEmpty()) {
                            binding.phoneInput.setText(phone);
                        }
                    } else {
                        // If document doesn't exist, create it with basic info
                        db.collection("users").document(userId)
                            .set(new java.util.HashMap<String, Object>() {{
                                put("name", "");
                                put("phone", "");
                                put("email", currentEmail);
                            }})
                            .addOnFailureListener(e -> 
                                Toast.makeText(ProfileActivity.this, 
                                    "Failed to initialize profile: " + e.getMessage(), 
                                    Toast.LENGTH_SHORT).show()
                            );
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setupSaveButton() {
        binding.saveButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString().trim();
            String phone = binding.phoneInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();

            if (name.isEmpty()) {
                binding.nameLayout.setError("Name is required");
                return;
            }

            if (email.isEmpty()) {
                binding.emailLayout.setError("Email is required");
                return;
            }

            // Show loading state
            binding.saveButton.setEnabled(false);
            binding.saveButton.setText("Saving...");

            String userId = currentUser.getUid();
            
            // First update Firestore data
            db.collection("users").document(userId)
                    .update("name", name, "phone", phone, "email", email)
                    .addOnSuccessListener(aVoid -> {
                        // Then update email in Firebase Auth if it changed
                        if (!email.equals(currentUser.getEmail())) {
                            currentUser.updateEmail(email)
                                .addOnSuccessListener(aVoid2 -> {
                                    showSuccessAndReset();
                                })
                                .addOnFailureListener(e -> {
                                    showError("Failed to update email: " + e.getMessage());
                                });
                        } else {
                            showSuccessAndReset();
                        }
                    })
                    .addOnFailureListener(e -> {
                        showError("Failed to update profile: " + e.getMessage());
                    });
        });
    }

    private void showSuccessAndReset() {
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        binding.saveButton.setEnabled(true);
        binding.saveButton.setText("Save");
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        binding.saveButton.setEnabled(true);
        binding.saveButton.setText("Save");
    }
}