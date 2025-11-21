package com.luxevista.resort.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luxevista.resort.R;
import com.luxevista.resort.models.RoomBooking;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RoomBookingAdapter extends RecyclerView.Adapter<RoomBookingAdapter.RoomBookingViewHolder> {
    private List<RoomBooking> bookings;
    private OnCancelClickListener listener;

    public interface OnCancelClickListener {
        void onCancelClick(int position);
    }

    public RoomBookingAdapter(List<RoomBooking> bookings, OnCancelClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_booking, parent, false);
        return new RoomBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomBookingViewHolder holder, int position) {
        RoomBooking booking = bookings.get(position);
        holder.bind(booking);
        
        // Set click listener for cancel button
        holder.cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    public void updateBookings(List<RoomBooking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    static class RoomBookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomNameTextView;
        private final TextView checkInTextView;
        private final TextView checkOutTextView;
        private final TextView statusTextView;
        private final Button cancelButton;

        RoomBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.room_name);
            checkInTextView = itemView.findViewById(R.id.check_in_date);
            checkOutTextView = itemView.findViewById(R.id.check_out_date);
            statusTextView = itemView.findViewById(R.id.booking_status);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }

        void bind(RoomBooking booking) {
            if (booking == null) return;

            roomNameTextView.setText(booking.getRoomName());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            checkInTextView.setText(dateFormat.format(booking.getCheckInDate()));
            checkOutTextView.setText(dateFormat.format(booking.getCheckOutDate()));
            
            String status = booking.getStatus();
            statusTextView.setText(status);
            
            // Set status background color based on status
            int statusColor;
            switch (status != null ? status.toLowerCase() : "") {
                case "confirmed":
                    statusColor = R.color.green;
                    break;
                case "cancelled":
                    statusColor = R.color.red;
                    break;
                case "pending":
                    statusColor = R.color.orange;
                    break;
                default:
                    statusColor = R.color.gray;
            }
            statusTextView.setBackgroundResource(statusColor);

            // Show cancel button only for confirmed bookings
            cancelButton.setVisibility(status != null && status.equalsIgnoreCase("Confirmed") 
                ? View.VISIBLE : View.GONE);
        }
    }
} 