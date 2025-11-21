package com.luxevista.resort;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.luxevista.resort.R;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Toast;
import android.widget.EditText;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luxevista.resort.models.RoomBooking;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.luxevista.resort.models.Room;
import java.util.Date;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private static final String TAG = "RoomAdapter";
    private Context context;
    private List<Room> rooms;
    private OnRoomClickListener listener;
    private double minPrice = 0;
    private double maxPrice = Double.MAX_VALUE;
    private String currentSort = "price_asc";

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
        void onBookClick(Room room);
        void onCancelClick(Room room);
    }

    public RoomAdapter(Context context, List<Room> rooms, OnRoomClickListener listener) {
        this.context = context;
        this.rooms = rooms;
        this.listener = listener;
    }

    public void updateRooms(List<Room> newRooms) {
        Log.d(TAG, "Updating rooms list with " + newRooms.size() + " rooms");
        this.rooms = new ArrayList<>(newRooms);
        notifyDataSetChanged();
    }

    public void setPriceRange(double min, double max) {
        this.minPrice = min;
        this.maxPrice = max;
        applyFiltersAndSort();
    }

    public void setSort(String sortType) {
        this.currentSort = sortType;
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        List<Room> filteredList = new ArrayList<>();
        
        // Apply price filter
        for (Room room : rooms) {
            if (room.getPrice() >= minPrice && room.getPrice() <= maxPrice) {
                filteredList.add(room);
            }
        }

        // Apply sorting
        Collections.sort(filteredList, new Comparator<Room>() {
            @Override
            public int compare(Room r1, Room r2) {
                switch (currentSort) {
                    case "price_asc":
                        return Double.compare(r1.getPrice(), r2.getPrice());
                    case "price_desc":
                        return Double.compare(r2.getPrice(), r1.getPrice());
                    case "name_asc":
                        return r1.getName().compareToIgnoreCase(r2.getName());
                    case "name_desc":
                        return r2.getName().compareToIgnoreCase(r1.getName());
                    default:
                        return 0;
                }
            }
        });

        this.rooms = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        Log.d(TAG, "Binding room: " + room.getName() + ", isBooked: " + room.isBooked());
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView roomImage;
        private TextView roomName;
        private TextView roomDescription;
        private TextView roomPrice;
        private TextView bookingTime;
        private Button bookButton;
        private Button cancelButton;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.room_image);
            roomName = itemView.findViewById(R.id.room_name);
            roomDescription = itemView.findViewById(R.id.room_description);
            roomPrice = itemView.findViewById(R.id.room_price);
            bookingTime = itemView.findViewById(R.id.booking_time);
            bookButton = itemView.findViewById(R.id.book_button);
            cancelButton = itemView.findViewById(R.id.cancel_button);

            if (roomImage == null || roomName == null || roomDescription == null || 
                roomPrice == null || bookingTime == null || bookButton == null || 
                cancelButton == null) {
                Log.e(TAG, "One or more views not found in layout");
                return;
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRoomClick(rooms.get(position));
                }
            });

            bookButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookClick(rooms.get(position));
                }
            });

            cancelButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCancelClick(rooms.get(position));
                }
            });
        }

        void bind(Room room) {
            Log.d(TAG, "Binding room data: " + room.toString());
            roomName.setText(room.getName());
            roomDescription.setText(room.getDescription());
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            roomPrice.setText(currencyFormat.format(room.getPrice()));

            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Log.d(TAG, "Loading image for room: " + room.getName());
                Glide.with(context)
                    .load(room.getImageUrl())
                    .placeholder(R.drawable.placeholder_room)
                    .error(R.drawable.error_image)
                    .into(roomImage);
            } else {
                Log.d(TAG, "No image URL for room: " + room.getName());
                roomImage.setImageResource(R.drawable.placeholder_room);
            }

            if (room.isBooked()) {
                Log.d(TAG, "Room is booked: " + room.getName());
                bookButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                bookingTime.setVisibility(View.VISIBLE);
                
                String bookingInfo = String.format("Booked from %s %s to %s %s",
                    room.getCheckInDate(), room.getCheckInTime(),
                    room.getCheckOutDate(), room.getCheckOutTime());
                bookingTime.setText(bookingInfo);
            } else {
                Log.d(TAG, "Room is available: " + room.getName());
                bookButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
                bookingTime.setVisibility(View.GONE);
                bookingTime.setText("");
            }
        }
    }

    private void showBookingDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_room_booking, null);
        builder.setView(dialogView);

        EditText checkInDate = dialogView.findViewById(R.id.check_in_date);
        EditText checkOutDate = dialogView.findViewById(R.id.check_out_date);
        EditText checkInTime = dialogView.findViewById(R.id.check_in_time);
        EditText checkOutTime = dialogView.findViewById(R.id.check_out_time);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancel_button);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirm_button);

        AlertDialog dialog = builder.create();

        // Date picker for check-in date
        checkInDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    checkInDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Date picker for check-out date
        checkOutDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    checkOutDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            
            // Set minimum date to check-in date if it's already selected
            if (!checkInDate.getText().toString().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date checkIn = sdf.parse(checkInDate.getText().toString());
                    calendar.setTime(checkIn);
                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing check-in date", e);
                }
            } else {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            }
            datePickerDialog.show();
        });

        // Time picker for check-in time
        checkInTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    checkInTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });

        // Time picker for check-out time
        checkOutTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    checkOutTime.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            );
            timePickerDialog.show();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        confirmButton.setOnClickListener(v -> {
            String checkInDateStr = checkInDate.getText().toString();
            String checkOutDateStr = checkOutDate.getText().toString();
            String checkInTimeStr = checkInTime.getText().toString();
            String checkOutTimeStr = checkOutTime.getText().toString();

            if (checkInDateStr.isEmpty() || checkOutDateStr.isEmpty() || 
                checkInTimeStr.isEmpty() || checkOutTimeStr.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date checkIn = sdf.parse(checkInDateStr + " " + checkInTimeStr);
                Date checkOut = sdf.parse(checkOutDateStr + " " + checkOutTimeStr);

                if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                    Toast.makeText(context, "Check-out must be after check-in", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for existing bookings
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("bookings")
                    .whereEqualTo("roomId", room.getId())
                    .whereEqualTo("status", "Confirmed")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(context, "This room is already booked", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create booking with date and time
                        RoomBooking booking = new RoomBooking();
                        booking.setRoomId(room.getId());
                        booking.setRoomName(room.getName());
                        booking.setCheckInDate(checkInDateStr);
                        booking.setCheckOutDate(checkOutDateStr);
                        booking.setCheckInTime(checkInTimeStr);
                        booking.setCheckOutTime(checkOutTimeStr);
                        booking.setStatus("Confirmed");
                        booking.setTotalPrice(room.getPrice());
                        booking.setCreatedAt(Timestamp.now());

                        // Save booking to Firestore
                        db.collection("bookings")
                            .add(booking)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(context, "Room booked successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                // Update the local room object
                                room.setBooked(true);
                                room.setCheckInDate(checkInDateStr);
                                room.setCheckInTime(checkInTimeStr);
                                room.setCheckOutDate(checkOutDateStr);
                                room.setCheckOutTime(checkOutTimeStr);
                                notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to book room: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error checking room availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            } catch (Exception e) {
                Toast.makeText(context, "Invalid date/time format", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error parsing dates", e);
            }
        });

        dialog.show();
    }
}