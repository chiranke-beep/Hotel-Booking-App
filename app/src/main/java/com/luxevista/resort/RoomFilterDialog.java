package com.luxevista.resort;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RoomFilterDialog extends DialogFragment {
    private OnFilterListener listener;
    private RadioGroup sortGroup;
    private Button applyButton;
    private Button resetButton;

    public interface OnFilterListener {
        void onFilterApplied(String sortType);
        void onFilterReset();
    }

    public void setOnFilterListener(OnFilterListener listener) {
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
        View view = inflater.inflate(R.layout.dialog_room_filter, container, false);

        sortGroup = view.findViewById(R.id.sort_group);
        applyButton = view.findViewById(R.id.apply_button);
        resetButton = view.findViewById(R.id.reset_button);

        applyButton.setOnClickListener(v -> applyFilter());
        resetButton.setOnClickListener(v -> resetFilter());

        return view;
    }

    private void applyFilter() {
        if (listener != null) {
            // Get sort type
            String sortType = "price_asc";
            if (sortGroup != null) {
                int checkedId = sortGroup.getCheckedRadioButtonId();
                if (checkedId == R.id.sort_price_asc) {
                    sortType = "price_asc";
                } else if (checkedId == R.id.sort_price_desc) {
                    sortType = "price_desc";
                } else if (checkedId == R.id.sort_name_asc) {
                    sortType = "name_asc";
                } else if (checkedId == R.id.sort_name_desc) {
                    sortType = "name_desc";
                }
            }

            listener.onFilterApplied(sortType);
            dismiss();
        }
    }

    private void resetFilter() {
        if (listener != null) {
            listener.onFilterReset();
            dismiss();
        }
    }
} 