package com.luxevista.resort.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luxevista.resort.R;
import com.luxevista.resort.adapters.ServiceBookingAdapter;
import com.luxevista.resort.models.ServiceBooking;

import java.util.ArrayList;
import java.util.List;

public class ServiceBookingHistoryFragment extends Fragment implements ServiceBookingAdapter.OnServiceBookingClickListener {
    private static final String TAG = "ServiceBookingHistory";
    private RecyclerView recyclerView;
    private ServiceBookingAdapter adapter;
    private List<ServiceBooking> bookingList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView errorView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_booking_history, container, false);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.booking_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        errorView = view.findViewById(R.id.error_view);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        bookingList = new ArrayList<>();
        adapter = new ServiceBookingAdapter(bookingList);
        adapter.setOnServiceBookingClickListener(this);
        recyclerView.setAdapter(adapter);
        
        db = FirebaseFirestore.getInstance();
        loadBookings();
        
        return view;
    }

    private void loadBookings() {
        showLoading(true);
        hideError();
        hideEmpty();

        // Check if user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showError("Please log in to view your bookings");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .whereNotEqualTo("serviceId", null)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                bookingList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        ServiceBooking booking = document.toObject(ServiceBooking.class);
                        if (booking != null) {
                            booking.setId(document.getId());
                            bookingList.add(booking);
                        } else {
                            Log.e(TAG, "Failed to convert document to ServiceBooking: " + document.getId());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing booking document: " + document.getId(), e);
                    }
                }
                adapter.notifyDataSetChanged();
                showLoading(false);
                
                if (bookingList.isEmpty()) {
                    showEmpty("You haven't booked any services yet");
                } else {
                    hideEmpty();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading service bookings", e);
                showError("Unable to load your bookings. Please try again later.");
            });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(message);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideError() {
        errorView.setVisibility(View.GONE);
    }

    private void showEmpty(String message) {
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText(message);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onCancelClick(ServiceBooking booking) {
        // Show confirmation dialog
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes", (dialog, which) -> {
                showLoading(true);
                // Update booking status
                db.collection("bookings")
                    .document(booking.getId())
                    .update("status", "Cancelled")
                    .addOnSuccessListener(aVoid -> {
                        // Update service status
                        db.collection("services")
                            .document(booking.getServiceId())
                            .update(
                                "status", "Available",
                                "userId", null,
                                "bookingDate", null,
                                "bookingTime", null
                            )
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), 
                                    "Booking cancelled successfully", 
                                    Toast.LENGTH_SHORT).show();
                                loadBookings(); // Refresh the list
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating service status", e);
                                showError("Failed to update service status: " + e.getMessage());
                            });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error cancelling booking", e);
                        showError("Failed to cancel booking: " + e.getMessage());
                    });
            })
            .setNegativeButton("No", null)
            .show();
    }
} 