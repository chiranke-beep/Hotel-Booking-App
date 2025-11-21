package com.luxevista.resort;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting LoginActivity");
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        if (emailInput == null || passwordInput == null || loginButton == null || registerButton == null) {
            Log.e(TAG, "One or more views not found!");
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Views initialized successfully");

        // Set click listeners
        loginButton.setOnClickListener(v -> login());
        registerButton.setOnClickListener(v -> {
            Log.d(TAG, "Register button clicked");
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void login() {
        Log.d(TAG, "Attempting login");
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Log.w(TAG, "Empty email or password");
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Attempt login
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    // Show success message
                    Toast.makeText(LoginActivity.this, 
                        "Login successful! Welcome back to Luxe Vista Resort",
                        Toast.LENGTH_LONG).show();
                    // Navigate to dashboard and clear the activity stack
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, 
                        "Authentication failed: " + task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
                    // Reset button state
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: LoginActivity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: LoginActivity resumed");
    }
} 