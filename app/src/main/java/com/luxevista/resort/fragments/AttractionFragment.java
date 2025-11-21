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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.luxevista.resort.R;
import com.luxevista.resort.adapters.AttractionAdapter;
import com.luxevista.resort.models.Attraction;

import java.util.ArrayList;
import java.util.List;

public class AttractionFragment extends Fragment {
    private static final String TAG = "AttractionFragment";
    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractions;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Initializing fragment");
        db = FirebaseFirestore.getInstance();
        attractions = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating view");
        View view = inflater.inflate(R.layout.fragment_attraction, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        if (recyclerView == null) {
            Log.e(TAG, "onCreateView: RecyclerView not found in layout");
            return view;
        }
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new AttractionAdapter(attractions, new AttractionAdapter.OnAttractionClickListener() {
            @Override
            public void onAttractionClick(Attraction attraction) {
                // Handle attraction click
            }
        });
        recyclerView.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Loading attractions");
        loadAttractions();
    }

    private void loadAttractions() {
        Log.d(TAG, "loadAttractions: Starting to load attractions from Firestore");
        Toast.makeText(getContext(), "Loading attractions...", Toast.LENGTH_SHORT).show();
        
        db.collection("attractions")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "loadAttractions: Successfully retrieved documents");
                    attractions.clear();
                    
                    if (task.getResult().isEmpty()) {
                        Log.d(TAG, "loadAttractions: No attractions found in collection");
                        Toast.makeText(getContext(), "No attractions found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            // Log the raw document data
                            Log.d(TAG, "Document data: " + document.getData().toString());
                            
                            // Create a new Attraction object
                            Attraction attraction = new Attraction();
                            attraction.setId(document.getId());
                            attraction.setName(document.getString("name"));
                            attraction.setDescription(document.getString("description"));
                            attraction.setImageUrl(document.getString("imageUrl"));
                            
                            Log.d(TAG, "Created attraction: " + attraction.getName());
                            attractions.add(attraction);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing document " + document.getId() + ": " + e.getMessage(), e);
                        }
                    }
                    
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "loadAttractions: Adapter updated with " + attractions.size() + " attractions");
                        if (attractions.size() > 0) {
                            Log.d(TAG, "First attraction in list: " + attractions.get(0).getName());
                        }
                    } else {
                        Log.e(TAG, "loadAttractions: Adapter is null");
                    }
                } else {
                    Log.e(TAG, "loadAttractions: Error getting documents", task.getException());
                    Toast.makeText(getContext(), "Error loading attractions: " + task.getException().getMessage(), 
                            Toast.LENGTH_SHORT).show();
                }
            });
    }
} 