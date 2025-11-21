package com.luxevista.resort;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luxevista.resort.models.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomListActivity extends AppCompatActivity implements RoomFilterDialog.OnFilterListener, RoomAdapter.OnRoomClickListener {
    private static final String TAG = "RoomListActivity";
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> rooms = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FloatingActionButton fabFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        Log.d(TAG, "onCreate: Starting RoomListActivity");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        fabFilter = findViewById(R.id.fab_filter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter(this, rooms, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firebase initialized");

        // Setup filter FAB
        fabFilter.setOnClickListener(v -> showFilterDialog());

        loadRooms();
    }

    private void loadRooms() {
        showLoading(true);
        Toast.makeText(this, "Loading rooms...", Toast.LENGTH_SHORT).show();
        
        db.collection("room")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                rooms.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Room room = document.toObject(Room.class);
                    room.setId(document.getId());
                    rooms.add(room);
                }
                adapter.updateRooms(rooms);
                showLoading(false);
                showEmptyView(rooms.isEmpty());
                
                // Add success message
                if (!rooms.isEmpty()) {
                    Toast.makeText(this, "Successfully loaded " + rooms.size() + " rooms", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(this, "Error loading rooms: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        dialog.show(getSupportFragmentManager(), "RoomFilterDialog");
    }

    @Override
    public void onFilterApplied(String sortType) {
        String message;
        switch (sortType) {
            case "price_asc":
                message = "Sorting rooms by price (low to high)";
                break;
            case "price_desc":
                message = "Sorting rooms by price (high to low)";
                break;
            case "name_asc":
                message = "Sorting rooms by name (A to Z)";
                break;
            case "name_desc":
                message = "Sorting rooms by name (Z to A)";
                break;
            default:
                message = "Sorting rooms";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        adapter.setSort(sortType);
    }

    @Override
    public void onFilterReset() {
        Toast.makeText(this, "Resetting room filters", Toast.LENGTH_SHORT).show();
        adapter.setSort("price_asc");
    }

    @Override
    public void onRoomClick(Room room) {
        // Show room details
        Toast.makeText(this, "Room: " + room.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookClick(Room room) {
        if (room.isBooked()) {
            Toast.makeText(this, "This room is already booked", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Please login to book a room", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "User is authenticated: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Show booking dialog
        BookingDialog dialog = new BookingDialog();
        dialog.setRoom(room);
        dialog.setOnBookingListener(new BookingDialog.OnBookingListener() {
            @Override
            public void onBookingConfirmed(Room room, String checkInDate, String checkOutDate, 
                                          String checkInTime, String checkOutTime) {
                showBookingConfirmationDialog(room, checkInDate, checkOutDate, checkInTime, checkOutTime);
            }

            @Override
            public void onBookingCancelled(Room room) {
                Log.d(TAG, "Booking cancelled for room: " + room.getName());
                Toast.makeText(RoomListActivity.this, "Booking cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "BookingDialog");
    }

    private void showBookingConfirmationDialog(Room room, String checkInDate, String checkOutDate, 
                                             String checkInTime, String checkOutTime) {
        // Calculate total price
        final double totalPrice = calculateTotalPrice(room, checkInDate, checkOutDate);

        // Show confirmation dialog with price details
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_booking_confirmation, null);
        TextView detailsText = dialogView.findViewById(R.id.booking_details);
        CheckBox termsCheckbox = dialogView.findViewById(R.id.terms_checkbox);
        
        detailsText.setText(String.format(
            "Room: %s\n" +
            "Check-in: %s %s\n" +
            "Check-out: %s %s\n" +
            "Number of days: %d\n" +
            "Price per day: $%.2f\n" +
            "Total price: $%.2f",
            room.getName(),
            checkInDate, checkInTime,
            checkOutDate, checkOutTime,
            (int)(totalPrice / room.getPrice()),
            room.getPrice(),
            totalPrice
        ));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle("Confirm Booking")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Confirm", null) // Set to null initially
            .setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            confirmButton.setEnabled(false);
            
            termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                confirmButton.setEnabled(isChecked);
            });

            confirmButton.setOnClickListener(v -> {
                if (termsCheckbox.isChecked()) {
                    dialog.dismiss();
                    createBooking(room, checkInDate, checkOutDate, checkInTime, checkOutTime, totalPrice);
                } else {
                    Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void createBooking(Room room, String checkInDate, String checkOutDate, 
                             String checkInTime, String checkOutTime, double totalPrice) {
        // Create booking in Firestore
        Map<String, Object> booking = new HashMap<>();
        booking.put("room_id", room.getId());
        booking.put("room_name", room.getName());
        booking.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        booking.put("check_in_date", checkInDate);
        booking.put("check_out_date", checkOutDate);
        booking.put("check_in_time", checkInTime);
        booking.put("check_out_time", checkOutTime);
        booking.put("status", "Confirmed");
        booking.put("guests", 0);
        booking.put("total_price", totalPrice);

        Log.d(TAG, "Attempting to create booking for room: " + room.getName());
        Log.d(TAG, "Booking details: " + booking.toString());
        Log.d(TAG, "Current user ID: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        // First, check if the room is already booked
        db.collection("room")
            .document(room.getId())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                Log.d(TAG, "Room document exists: " + documentSnapshot.exists());
                Log.d(TAG, "Room data: " + documentSnapshot.getData());

                if (documentSnapshot.getBoolean("isBooked")) {
                    Log.d(TAG, "Room is already booked");
                    Toast.makeText(this, 
                        "This room is already booked", 
                        Toast.LENGTH_SHORT).show();
                    return;
                }

                // If not booked, proceed with creating the booking
                db.collection("bookings")
                    .add(booking)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Booking created successfully with ID: " + documentReference.getId());
                        
                        // Update room status
                        Map<String, Object> roomUpdate = new HashMap<>();
                        roomUpdate.put("isBooked", true);
                        roomUpdate.put("checkInDate", checkInDate);
                        roomUpdate.put("checkOutDate", checkOutDate);
                        roomUpdate.put("checkInTime", checkInTime);
                        roomUpdate.put("checkOutTime", checkOutTime);
                        roomUpdate.put("booking_id", documentReference.getId());

                        Log.d(TAG, "Updating room with data: " + roomUpdate.toString());

                        db.collection("room")
                            .document(room.getId())
                            .update(roomUpdate)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Room status updated successfully");
                                Toast.makeText(this, 
                                    "Room booked successfully from " + checkInDate + " " + checkInTime + 
                                    " to " + checkOutDate + " " + checkOutTime, 
                                    Toast.LENGTH_LONG).show();
                                loadRooms(); // Refresh the list
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating room status", e);
                                Toast.makeText(this, 
                                    "Error updating room status: " + e.getMessage(), 
                                    Toast.LENGTH_SHORT).show();

                                // If room update fails, delete the booking
                                db.collection("bookings")
                                    .document(documentReference.getId())
                                    .delete()
                                    .addOnFailureListener(deleteError -> {
                                        Log.e(TAG, "Error deleting failed booking", deleteError);
                                    });
                            });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create booking", e);
                        Toast.makeText(this, 
                            "Failed to create booking: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error checking room availability", e);
                Toast.makeText(this, 
                    "Error checking room availability: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private double calculateTotalPrice(Room room, String checkInDate, String checkOutDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date checkIn = dateFormat.parse(checkInDate);
            Date checkOut = dateFormat.parse(checkOutDate);
            long diffInMillis = checkOut.getTime() - checkIn.getTime();
            int numberOfDays = (int) (diffInMillis / (24 * 60 * 60 * 1000));
            return room.getPrice() * numberOfDays;
        } catch (Exception e) {
            Log.e("RoomListActivity", "Error calculating total price", e);
            return room.getPrice(); // Fallback to single day price
        }
    }

    @Override
    public void onCancelClick(Room room) {
        if (!room.isBooked()) {
            Toast.makeText(this, "This room is not booked", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel this booking?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // First, find the booking document
                db.collection("bookings")
                    .whereEqualTo("room_id", room.getId())
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
                                        Map<String, Object> roomUpdate = new HashMap<>();
                                        roomUpdate.put("isBooked", false);
                                        roomUpdate.put("checkInDate", null);
                                        roomUpdate.put("checkOutDate", null);
                                        roomUpdate.put("checkInTime", null);
                                        roomUpdate.put("checkOutTime", null);
                                        roomUpdate.put("booking_id", null);
                                        
                                        // Update room in Firestore
                                        db.collection("room")
                                            .document(room.getId())
                                            .update(roomUpdate)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(this, 
                                                    "Booking cancelled successfully", 
                                                    Toast.LENGTH_SHORT).show();
                                                loadRooms(); // Refresh the list
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, 
                                                    "Error updating room status: " + e.getMessage(), 
                                                    Toast.LENGTH_LONG).show();
                                            });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, 
                                            "Error cancelling booking: " + e.getMessage(), 
                                            Toast.LENGTH_LONG).show();
                                    });
                            }
                        } else {
                            Toast.makeText(this, 
                                "No active booking found for this room", 
                                Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, 
                            "Error finding booking: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
            })
            .setNegativeButton("No", null)
            .show();
    }
}