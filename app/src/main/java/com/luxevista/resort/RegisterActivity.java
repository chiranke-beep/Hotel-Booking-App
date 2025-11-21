package com.luxevista.resort;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton registerButton;
    private MaterialButton loginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);

        // Set click listeners
        registerButton.setOnClickListener(v -> register());
        loginButton.setOnClickListener(v -> {
            // Navigate to login activity
            finish(); // Close the current activity
        });
    }

    private void register() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        // Create user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    
                    // Update user profile with name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                    auth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(profileTask -> {
                            if (profileTask.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                // Show success message
                                Toast.makeText(RegisterActivity.this, 
                                    "Registration successful! Welcome to Luxe Vista Resort",
                                    Toast.LENGTH_LONG).show();
                                // Navigate to main activity
                                startActivity(new android.content.Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Log.w(TAG, "Failed to update profile", profileTask.getException());
                                Toast.makeText(RegisterActivity.this, 
                                    "Registration successful but failed to update profile",
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, 
                        "Registration failed: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
                    // Reset button state
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                }
            });
    }
}