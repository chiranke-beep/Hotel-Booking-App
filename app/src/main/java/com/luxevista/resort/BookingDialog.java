package com.luxevista.resort;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.luxevista.resort.models.Room;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingDialog extends DialogFragment {
    private static final String TAG = "BookingDialog";
    private Room room;
    private OnBookingListener listener;
    private DatePicker checkInDatePicker;
    private DatePicker checkOutDatePicker;
    private TimePicker checkInTimePicker;
    private TimePicker checkOutTimePicker;
    private Button nextButton;
    private Button confirmButton;
    private Button cancelButton;
    private LinearLayout dateSelectionLayout;
    private LinearLayout timeSelectionLayout;

    public interface OnBookingListener {
        void onBookingConfirmed(Room room, String checkInDate, String checkOutDate, 
                              String checkInTime, String checkOutTime);
        void onBookingCancelled(Room room);
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setOnBookingListener(OnBookingListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_booking, container, false);

        // Initialize views
        checkInDatePicker = view.findViewById(R.id.check_in_date_picker);
        checkOutDatePicker = view.findViewById(R.id.check_out_date_picker);
        checkInTimePicker = view.findViewById(R.id.check_in_time_picker);
        checkOutTimePicker = view.findViewById(R.id.check_out_time_picker);
        nextButton = view.findViewById(R.id.next_button);
        confirmButton = view.findViewById(R.id.confirm_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        dateSelectionLayout = view.findViewById(R.id.date_selection_layout);
        timeSelectionLayout = view.findViewById(R.id.time_selection_layout);

        // Set 24-hour format for time pickers
        checkInTimePicker.setIs24HourView(true);
        checkOutTimePicker.setIs24HourView(true);

        // Set minimum date to today
        Calendar calendar = Calendar.getInstance();
        checkInDatePicker.setMinDate(calendar.getTimeInMillis());
        checkOutDatePicker.setMinDate(calendar.getTimeInMillis());

        // Set initial check-out date to tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        checkOutDatePicker.updateDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Initially hide time selection
        timeSelectionLayout.setVisibility(View.GONE);
        confirmButton.setVisibility(View.GONE);

        // Set up button click listeners
        nextButton.setOnClickListener(v -> {
            // Validate dates
            Calendar checkInCalendar = Calendar.getInstance();
            checkInCalendar.set(checkInDatePicker.getYear(), checkInDatePicker.getMonth(), 
                              checkInDatePicker.getDayOfMonth());
            
            Calendar checkOutCalendar = Calendar.getInstance();
            checkOutCalendar.set(checkOutDatePicker.getYear(), checkOutDatePicker.getMonth(), 
                               checkOutDatePicker.getDayOfMonth());

            if (checkOutCalendar.before(checkInCalendar)) {
                Toast.makeText(getContext(), "Check-out date must be after check-in date", 
                    Toast.LENGTH_SHORT).show();
                return;
            }

            // Show time selection
            dateSelectionLayout.setVisibility(View.GONE);
            timeSelectionLayout.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
        });

        confirmButton.setOnClickListener(v -> confirmBooking());
        cancelButton.setOnClickListener(v -> cancelBooking());

        return view;
    }

    private void confirmBooking() {
        if (listener != null && room != null) {
            // Get selected dates
            Calendar checkInCalendar = Calendar.getInstance();
            checkInCalendar.set(checkInDatePicker.getYear(), checkInDatePicker.getMonth(), 
                              checkInDatePicker.getDayOfMonth());
            
            Calendar checkOutCalendar = Calendar.getInstance();
            checkOutCalendar.set(checkOutDatePicker.getYear(), checkOutDatePicker.getMonth(), 
                               checkOutDatePicker.getDayOfMonth());

            // Format dates and times
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            String checkInDate = dateFormat.format(checkInCalendar.getTime());
            String checkOutDate = dateFormat.format(checkOutCalendar.getTime());
            String checkInTime = String.format("%02d:%02d", 
                checkInTimePicker.getHour(), checkInTimePicker.getMinute());
            String checkOutTime = String.format("%02d:%02d", 
                checkOutTimePicker.getHour(), checkOutTimePicker.getMinute());

            listener.onBookingConfirmed(room, checkInDate, checkOutDate, checkInTime, checkOutTime);
            dismiss();
        }
    }

    private void cancelBooking() {
        if (listener != null && room != null) {
            listener.onBookingCancelled(room);
        }
        dismiss();
    }
} 