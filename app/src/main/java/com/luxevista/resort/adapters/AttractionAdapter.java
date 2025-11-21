package com.luxevista.resort.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luxevista.resort.R;
import com.luxevista.resort.models.Attraction;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {
    private List<Attraction> attractions;
    private OnAttractionClickListener listener;

    public interface OnAttractionClickListener {
        void onAttractionClick(Attraction attraction);
    }

    public AttractionAdapter(List<Attraction> attractions, OnAttractionClickListener listener) {
        this.attractions = attractions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractions.get(position);
        holder.bind(attraction);
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    public void updateAttractions(List<Attraction> newAttractions) {
        this.attractions = newAttractions;
        notifyDataSetChanged();
    }

    class AttractionViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView descriptionTextView;

        AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAttractionClick(attractions.get(position));
                }
            });
        }

        void bind(Attraction attraction) {
            nameTextView.setText(attraction.getName());
            descriptionTextView.setText(attraction.getDescription());
            
            Glide.with(itemView.getContext())
                    .load(attraction.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView);
        }
    }
} 