package com.luxevista.resort.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.luxevista.resort.R;
import com.luxevista.resort.models.Service;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> services;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onBookClick(Service service);
        void onCancelClick(Service service);
    }

    public ServiceAdapter(List<Service> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;
        private TextView statusTextView;
        private MaterialButton bookButton;
        private MaterialButton cancelButton;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
            bookButton = itemView.findViewById(R.id.book_button);
            cancelButton = itemView.findViewById(R.id.cancel_button);

            bookButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookClick(services.get(position));
                }
            });

            cancelButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCancelClick(services.get(position));
                }
            });
        }

        void bind(Service service) {
            nameTextView.setText(service.getName());
            descriptionTextView.setText(service.getDescription());
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            priceTextView.setText(currencyFormat.format(service.getPrice()));
            
            statusTextView.setText(service.getStatus());
            
            if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(service.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.placeholder_image);
            }

            if ("Booked".equals(service.getStatus())) {
                bookButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
            } else {
                bookButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.GONE);
            }
        }
    }
}