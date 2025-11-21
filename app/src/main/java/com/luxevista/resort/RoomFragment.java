package com.luxevista.resort;

import android.content.Context;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luxevista.resort.models.Room;
import com.luxevista.resort.models.RoomBooking;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment implements RoomAdapter.OnRoomClickListener, RoomFilterDialog.OnFilterListener {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);
        
        recyclerView = view.findViewById(R.id.room_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        roomList = new ArrayList<>();
        adapter = new RoomAdapter(requireContext(), roomList, this);
        recyclerView.setAdapter(adapter);
        
        db = FirebaseFirestore.getInstance();
        loadRooms();
        
        FloatingActionButton fabFilter = view.findViewById(R.id.filter_fab);
        fabFilter.setOnClickListener(v -> showFilterDialog());
        
        return view;
    }

    private void loadRooms() {
        showLoading(true);
        Toast.makeText(requireContext(), "Loading rooms...", Toast.LENGTH_SHORT).show();
        db.collection("room")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    roomList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Room room = document.toObject(Room.class);
                        room.setId(document.getId());
                        roomList.add(room);
                    }
                    adapter.updateRooms(roomList);
                    showLoading(false);
                    showEmptyView(roomList.isEmpty());
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Error loading rooms: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(show ? View.GONE : emptyView.getVisibility());
    }

    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showFilterDialog() {
        RoomFilterDialog dialog = new RoomFilterDialog();
        dialog.setOnFilterListener(this);
        dialog.show(getChildFragmentManager(), "RoomFilterDialog");
    }

    @Override
    public void onRoomClick(Room room) {
        // Show room details
        Toast.makeText(getContext(), "Room: " + room.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookClick(Room room) {
        if (room.isBooked()) {
            Toast.makeText(getContext(), "This room is already booked", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show booking dialog
        BookingDialog dialog = new BookingDialog();
        dialog.setRoom(room);
        dialog.setOnBookingListener(new BookingDialog.OnBookingListener() {
            @Override
            public void onBookingConfirmed(Room room, String checkInDate, String checkOutDate, 
                                         String checkInTime, String checkOutTime) {
                // Update room booking status
                room.setBooked(true);
                room.setCheckInDate(checkInDate);
                room.setCheckOutDate(checkOutDate);
                room.setCheckInTime(checkInTime);
                room.setCheckOutTime(checkOutTime);
                
                // Update in Firestore
                db.collection("room")
                    .document(room.getId())
                    .update(
                        "isBooked", true,
                        "checkInDate", checkInDate,
                        "checkOutDate", checkOutDate,
                        "checkInTime", checkInTime,
                        "checkOutTime", checkOutTime
                    )
                    .addOnSuccessListener(aVoid -> {
                        // Create a new booking document
                        RoomBooking booking = new RoomBooking();
                        booking.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        booking.setRoomId(room.getId());
                        booking.setRoomName(room.getName());
                        booking.setCheckInDate(checkInDate);
                        booking.setCheckOutDate(checkOutDate);
                        booking.setCheckInTime(checkInTime);
                        booking.setCheckOutTime(checkOutTime);
                        booking.setTotalPrice(room.getPrice());
                        booking.setStatus("Confirmed");
                        booking.setTimestamp(System.currentTimeMillis());
                        booking.setGuests(1); // Add default number of guests

                        // First check if the room is already booked
                        db.collection("bookings")
                            .whereEqualTo("roomId", room.getId())
                            .whereEqualTo("status", "Confirmed")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(getContext(), "This room is already booked", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Create the booking if room is available
                                db.collection("bookings")
                                    .add(booking)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getContext(), 
                                            "Room booked successfully from " + checkInDate + " " + checkInTime + 
                                            " to " + checkOutDate + " " + checkOutTime, 
                                            Toast.LENGTH_LONG).show();
                                        adapter.updateRooms(roomList);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), 
                                            "Error creating booking: " + e.getMessage(), 
                                            Toast.LENGTH_LONG).show();
                                    });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), 
                                    "Error checking room availability: " + e.getMessage(), 
                                    Toast.LENGTH_LONG).show();
                            });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), 
                            "Error booking room: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
            }

            @Override
            public void onBookingCancelled(Room room) {
                // No action needed when booking is cancelled
                Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getChildFragmentManager(), "BookingDialog");
    }

    @Override
    public void onCancelClick(Room room) {
        if (!room.isBooked()) {
            Toast.makeText(getContext(), "This room is not booked", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new android.app.AlertDialog.Builder(getContext())
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // First, find the booking document
                db.collection("bookings")
                    .whereEqualTo("roomId", room.getId())
                    .whereEqualTo("status", "Confirmed")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Update the booking status
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                db.collection("bookings")
                                    .document(document.getId())
                                    .update("status", "Cancelled")
                                    .addOnSuccessListener(aVoid -> {
                                        // Update room booking status
                                        room.setBooked(false);
                                        room.setCheckInDate(null);
                                        room.setCheckOutDate(null);
                                        room.setCheckInTime(null);
                                        room.setCheckOutTime(null);
                                        
                                        // Update room in Firestore
                                        db.collection("room")
                                            .document(room.getId())
                                            .update(
                                                "isBooked", false,
                                                "checkInDate", null,
                                                "checkOutDate", null,
                                                "checkInTime", null,
                                                "checkOutTime", null
                                            )
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(getContext(), 
                                                    "Booking cancelled successfully", 
                                                    Toast.LENGTH_SHORT).show();
                                                adapter.updateRooms(roomList);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), 
                                                    "Error updating room status: " + e.getMessage(), 
                                                    Toast.LENGTH_LONG).show();
                                            });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), 
                                            "Error cancelling booking: " + e.getMessage(), 
                                            Toast.LENGTH_LONG).show();
                                    });
                            }
                        } else {
                            Toast.makeText(getContext(), 
                                "No active booking found for this room", 
                                Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), 
                            "Error finding booking: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
            })
            .setNegativeButton("No", null)
            .show();
    }

    @Override
    public void onFilterApplied(String sortType) {
        // Sort rooms
        List<Room> sortedRooms = new ArrayList<>(roomList);
        switch (sortType) {
            case "price_asc":
                sortedRooms.sort((r1, r2) -> Double.compare(r1.getPrice(), r2.getPrice()));
                break;
            case "price_desc":
                sortedRooms.sort((r1, r2) -> Double.compare(r2.getPrice(), r1.getPrice()));
                break;
            case "name_asc":
                sortedRooms.sort((r1, r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
                break;
            case "name_desc":
                sortedRooms.sort((r1, r2) -> r2.getName().compareToIgnoreCase(r1.getName()));
                break;
        }

        adapter.updateRooms(sortedRooms);
    }

    @Override
    public void onFilterReset() {
        adapter.updateRooms(roomList);
    }
} 