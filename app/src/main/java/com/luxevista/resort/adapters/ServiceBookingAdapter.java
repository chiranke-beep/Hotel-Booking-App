package com.luxevista.resort.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luxevista.resort.R;
import com.luxevista.resort.models.ServiceBooking;

import java.util.List;

public class ServiceBookingAdapter extends RecyclerView.Adapter<ServiceBookingAdapter.ServiceBookingViewHolder> {
    private List<ServiceBooking> bookings;
    private OnServiceBookingClickListener listener;

    public interface OnServiceBookingClickListener {
        void onCancelClick(ServiceBooking booking);
    }

    public ServiceBookingAdapter(List<ServiceBooking> bookings) {
        this.bookings = bookings;
    }

    public void setOnServiceBookingClickListener(OnServiceBookingClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceBookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_service_booking, parent, false);
        return new ServiceBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceBookingViewHolder holder, int position) {
        ServiceBooking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class ServiceBookingViewHolder extends RecyclerView.ViewHolder {
        private TextView serviceNameTextView;
        private TextView bookingDateTextView;
        private TextView statusTextView;
        private Button cancelButton;

        ServiceBookingViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.service_name);
            bookingDateTextView = itemView.findViewById(R.id.booking_date);
            statusTextView = itemView.findViewById(R.id.booking_status);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }

        void bind(ServiceBooking booking) {
            serviceNameTextView.setText(booking.getServiceName());
            bookingDateTextView.setText(String.format("Date: %s\nTime: %s", 
                booking.getBookingDate(), booking.getBookingTime()));
            statusTextView.setText(booking.getStatus());
            
            // Set status background color based on status
            int statusColor;
            switch (booking.getStatus().toLowerCase()) {
                case "confirmed":
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_confirmed);
                    break;
                case "cancelled":
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_cancelled);
                    break;
                case "pending":
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_pending);
                    break;
                default:
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_default);
            }
            statusTextView.setBackgroundColor(statusColor);

            // Show cancel button only for confirmed bookings
            if (booking.getStatus().equalsIgnoreCase("confirmed")) {
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCancelClick(booking);
                    }
                });
            } else {
                cancelButton.setVisibility(View.GONE);
            }
        }
    }
} 