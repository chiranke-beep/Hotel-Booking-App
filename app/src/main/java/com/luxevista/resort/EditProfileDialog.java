package com.luxevista.resort;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileDialog extends DialogFragment {
    private ShapeableImageView profileImage;
    private TextInputEditText nameInput, phoneInput;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image_edit);
        nameInput = view.findViewById(R.id.name_input_edit);
        phoneInput = view.findViewById(R.id.phone_input_edit);

        // Load current user data
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameInput.setText(documentSnapshot.getString("name"));
                        phoneInput.setText(documentSnapshot.getString("phone"));
                    }
                });
        }

        // Setup change photo button
        ImageView changePhotoButton = view.findViewById(R.id.change_photo_button);
        changePhotoButton.setOnClickListener(v -> {
            // TODO: Implement photo picker
        });

        // Setup save button
        MaterialButton saveButton = view.findViewById(R.id.save_button_edit);
        saveButton.setOnClickListener(v -> saveProfile());

        // Setup cancel button
        MaterialButton cancelButton = view.findViewById(R.id.cancel_button_edit);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void saveProfile() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }

            db.collection("users").document(userId)
                .update("name", name, "phone", phone)
                .addOnSuccessListener(aVoid -> {
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
        }
    }
} 