package com.luxevista.resort;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class EmailNotificationHelper {
    private static final String TAG = "EmailNotificationHelper";
    private final Context context;
    private final FirebaseFirestore db;

    public EmailNotificationHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    public void sendBookingConfirmation(String userId, String bookingId) {
        // Get user email
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("email");
                    if (email != null) {
                        // Send email notification
                        sendEmail(email, "Booking Confirmation", 
                            "Your booking has been confirmed. Booking ID: " + bookingId);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Error sending confirmation email: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            });
    }

    public void sendBookingCancellation(String userId, String bookingId) {
        // Get user email
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String email = documentSnapshot.getString("email");
                    if (email != null) {
                        // Send email notification
                        sendEmail(email, "Booking Cancellation", 
                            "Your booking has been cancelled. Booking ID: " + bookingId);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Error sending cancellation email: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            });
    }

    private void sendEmail(String to, String subject, String body) {
        // TODO: Implement email sending logic
        // This is a placeholder for actual email sending implementation
        Toast.makeText(context, "Email notification sent to: " + to, Toast.LENGTH_SHORT).show();
    }
} 