package com.luxevista.resort.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luxevista.resort.R;
import com.luxevista.resort.adapters.ServiceAdapter;
import com.luxevista.resort.models.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ServiceFragment extends Fragment implements ServiceAdapter.OnServiceClickListener {
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> services;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FloatingActionButton fabBookingHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        services = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceAdapter(services, this);
        recyclerView.setAdapter(adapter);

        // Initialize booking history FAB
        fabBookingHistory = view.findViewById(R.id.fab_booking_history);
        fabBookingHistory.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(getContext(), "Please log in to view booking history", Toast.LENGTH_SHORT).show();
                return;
            }
            // Navigate to booking history
            ServiceBookingHistoryFragment fragment = new ServiceBookingHistoryFragment();
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please log in to view services", Toast.LENGTH_SHORT).show();
        }
        loadServices();
    }

    private void loadServices() {
        Log.d("ServiceFragment", "Loading services...");
        Toast.makeText(requireContext(), "Loading services...", Toast.LENGTH_SHORT).show();
        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    services.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("ServiceFragment", "No services found, adding spa service");
                        addSpaService();
                    } else {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Service service = doc.toObject(Service.class);
                            if (service != null) {
                                service.setId(doc.getId());
                                services.add(service);
                                Log.d("ServiceFragment", "Loaded service: " + service.getName() + " with ID: " + service.getId());
                            }
                        }
                        Log.d("ServiceFragment", "Loaded " + services.size() + " services");
                        adapter.notifyDataSetChanged();
                        
                        // Add success message
                        if (!services.isEmpty()) {
                            Toast.makeText(requireContext(), 
                                "Successfully loaded " + services.size() + " services", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ServiceFragment", "Failed to load services", e);
                    Toast.makeText(requireContext(), "Failed to load services: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addSpaService() {
        Service service = new Service();
        service.setId("service1");
        service.setName("Spa & Wellness");
        service.setDescription("Indulge in our luxurious spa treatments and wellness services.");
        service.setPrice(15000);
        service.setImageUrl("https://images.unsplash.com/photo-1544161515-4ab6ce6db874");
        service.setStatus("Available");

        db.collection("services")
            .document(service.getId())
            .set(service)
            .addOnSuccessListener(aVoid -> {
                Log.d("ServiceFragment", "Spa service created successfully");
                Toast.makeText(requireContext(), "Spa service added successfully", Toast.LENGTH_SHORT).show();
                loadServices();
            })
            .addOnFailureListener(e -> {
                Log.e("ServiceFragment", "Failed to create spa service", e);
                Toast.makeText(requireContext(), "Failed to add spa service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onBookClick(Service service) {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(getContext(), "Please log in to book a service", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ServiceFragment", "Attempting to book service with ID: " + service.getId());

        // Show date picker
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                // Show time picker after date is selected
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (timeView, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        
                        // Format date and time
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                        String bookingDate = dateFormat.format(calendar.getTime());
                        String bookingTime = timeFormat.format(calendar.getTime());

                        // Create booking in Firestore
                        Map<String, Object> booking = new HashMap<>();
                        booking.put("serviceId", service.getId());
                        booking.put("userId", userId);
                        booking.put("serviceName", service.getName());
                        booking.put("bookingDate", bookingDate);
                        booking.put("bookingTime", bookingTime);
                        booking.put("status", "Booked");
                        booking.put("price", service.getPrice());
                        booking.put("timestamp", FieldValue.serverTimestamp());

                        // First, check if the service is already booked
                        db.collection("services")
                            .document(service.getId())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (!documentSnapshot.exists()) {
                                    Log.e("ServiceFragment", "Service document not found with ID: " + service.getId());
                                    Toast.makeText(getContext(), "Service not found. Please try again.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String currentStatus = documentSnapshot.getString("status");
                                Log.d("ServiceFragment", "Current service status: " + currentStatus);
                                
                                if ("Booked".equals(currentStatus)) {
                                    Toast.makeText(getContext(), "This service is already booked", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // If not booked, proceed with creating the booking
                                db.collection("bookings")
                                    .add(booking)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d("ServiceFragment", "Booking created with ID: " + documentReference.getId());
                                        
                                        // Update service status
                                        Service updatedService = documentSnapshot.toObject(Service.class);
                                        if (updatedService != null) {
                                            updatedService.setStatus("Booked");
                                            updatedService.setUserId(userId);
                                            updatedService.setBookingDate(bookingDate);
                                            updatedService.setBookingTime(bookingTime);

                                            db.collection("services")
                                                .document(service.getId())
                                                .set(updatedService)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("ServiceFragment", "Service status updated successfully");
                                                    Toast.makeText(getContext(), "Service booked successfully", Toast.LENGTH_SHORT).show();
                                                    loadServices();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("ServiceFragment", "Error updating service status", e);
                                                    Toast.makeText(getContext(), "Failed to update service status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ServiceFragment", "Error creating booking", e);
                                        Toast.makeText(getContext(), "Failed to create booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ServiceFragment", "Error checking service status", e);
                                Toast.makeText(getContext(), "Failed to check service availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                );
                timePickerDialog.show();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onCancelClick(Service service) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Available");
        updates.put("userId", null);
        updates.put("bookingDate", null);
        updates.put("bookingTime", null);

        db.collection("services")
                .document(service.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), 
                        "Service booking cancelled successfully. A confirmation email has been sent to your registered email address.", 
                        Toast.LENGTH_LONG).show();
                    loadServices();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to cancel booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}