package com.luxevista.resort;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.luxevista.resort.R;

public class DashboardFragment extends Fragment {

    private MaterialCardView roomsCard, servicesCard, attractionsCard, profileCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize card views
        roomsCard = view.findViewById(R.id.rooms_card);
        servicesCard = view.findViewById(R.id.services_card);
        attractionsCard = view.findViewById(R.id.attractions_card);
        profileCard = view.findViewById(R.id.profile_card);

        // Set click listeners
        roomsCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), RoomListActivity.class)));
        servicesCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), ServiceListActivity.class)));
        attractionsCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), AttractionListActivity.class)));
        profileCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), ProfileActivity.class)));

        return view;
    }
}