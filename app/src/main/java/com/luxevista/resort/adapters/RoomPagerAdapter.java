package com.luxevista.resort.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luxevista.resort.fragments.RoomBookingHistoryFragment;
import com.luxevista.resort.fragments.ServiceBookingHistoryFragment;

public class RoomPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_TABS = 2;

    public RoomPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RoomBookingHistoryFragment();
            case 1:
                return new ServiceBookingHistoryFragment();
            default:
                return new RoomBookingHistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
} 