package com.luxevista.resort.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luxevista.resort.R;
import com.luxevista.resort.adapters.RoomBookingAdapter;
import com.luxevista.resort.models.RoomBooking;

import java.util.ArrayList;
import java.util.List;

public class RoomBookingHistoryFragment extends Fragment implements RoomBookingAdapter.OnCancelClickListener {
    private RecyclerView recyclerView;
    private RoomBookingAdapter adapter;
    private List<RoomBooking> bookingList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_booking_history, container, false);
        
        recyclerView = view.findViewById(R.id.booking_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        bookingList = new ArrayList<>();
        adapter = new RoomBookingAdapter(bookingList, this);
        recyclerView.setAdapter(adapter);
        
        db = FirebaseFirestore.getInstance();
        loadBookings();
        
        return view;
    }

    @Override
    public void onCancelClick(int position) {
        RoomBooking booking = bookingList.get(position);
        // Show confirmation dialog
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Update booking status in Firestore
                db.collection("bookings")
                    .document(booking.getId())
                    .update("status", "Cancelled")
                    .addOnSuccessListener(aVoid -> {
                        // Also update the room status
                        db.collection("room")
                            .document(booking.getRoomId())
                            .update("isBooked", false)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), 
                                    "Room booking cancelled successfully. A confirmation email has been sent to your registered email address.", 
                                    Toast.LENGTH_LONG).show();
                                loadBookings(); // Refresh the list
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), 
                                    "Booking cancelled but failed to update room status: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                                loadBookings(); // Still refresh the list
                            });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to cancel booking: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void loadBookings() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        db.collection("room")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                bookingList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    RoomBooking booking = document.toObject(RoomBooking.class);
                    booking.setId(document.getId());
                    bookingList.add(booking);
                }
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error loading bookings: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            });
    }
} 