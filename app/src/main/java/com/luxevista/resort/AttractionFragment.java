package com.luxevista.resort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luxevista.resort.adapters.AttractionAdapter;
import com.luxevista.resort.models.Attraction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttractionFragment extends Fragment {
    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private ArrayList<Attraction> attractions;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        attractions = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attraction, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
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
        loadAttractions();
    }

    private void loadAttractions() {
        Toast.makeText(requireContext(), "Loading attractions...", Toast.LENGTH_SHORT).show();
        db.collection("attractions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    attractions.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        // If no attractions exist, add some sample attractions
                        addSampleAttractions();
                    } else {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Attraction attraction = doc.toObject(Attraction.class);
                            if (attraction != null) {
                                attractions.add(attraction);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        
                        // Add success message
                        if (!attractions.isEmpty()) {
                            Toast.makeText(requireContext(), 
                                "Successfully loaded " + attractions.size() + " attractions", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to load attractions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addSampleAttractions() {
        // Add Water Park
        Map<String, Object> waterPark = new HashMap<>();
        waterPark.put("id", "attraction1");
        waterPark.put("name", "Water Park");
        waterPark.put("description", "Experience thrilling water slides and relaxing pools in our state-of-the-art water park.");
        waterPark.put("price", 2500);
        waterPark.put("imageUrl", "https://images.unsplash.com/photo-1575429198097-0414ec08e8cd?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1470&q=80");

        // Add Adventure Zone
        Map<String, Object> adventureZone = new HashMap<>();
        adventureZone.put("id", "attraction2");
        adventureZone.put("name", "Adventure Zone");
        adventureZone.put("description", "Test your limits with our exciting adventure activities including rock climbing and zip-lining.");
        adventureZone.put("price", 3000);
        adventureZone.put("imageUrl", "https://images.unsplash.com/photo-1517649763962-0c623066013b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1470&q=80");

        // Add both attractions
        db.collection("attractions")
                .document("attraction1")
                .set(waterPark)
                .addOnSuccessListener(aVoid -> {
                    db.collection("attractions")
                            .document("attraction2")
                            .set(adventureZone)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Sample attractions added successfully", Toast.LENGTH_SHORT).show();
                                loadAttractions();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to add adventure zone: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add water park: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
} 