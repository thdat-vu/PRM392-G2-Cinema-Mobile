package com.g2.moviebooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.g2.moviebooking.R;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    private List<Integer> seatStatusList;

    public SeatAdapter(List<Integer> seatStatusList) {
        this.seatStatusList = seatStatusList;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        int status = seatStatusList.get(position);
        holder.seatImage.setImageResource(status);
        holder.seatLabel.setText(getSeatLabel(position));

        // Set description based on seat status
        holder.seatImage.setContentDescription(getSeatDescription(status));

        // Handle click event for each seat
        holder.itemView.setOnClickListener(v -> {
            if (seatStatusList.get(position) == R.drawable.available_seat) {
                // Change to selected
                seatStatusList.set(position, R.drawable.seat_selected);
            } else if (seatStatusList.get(position) == R.drawable.seat_selected) {
                // Change to available again
                seatStatusList.set(position, R.drawable.available_seat);
            }
            // Notify that this item has changed
            notifyItemChanged(position);
        });
    }

    private String getSeatLabel(int position) {
        char row = (char) ('A' + (position / 8)); // Calculate row (A, B, C, ...)
        int seatNumber = (position % 8) + 1;      // Calculate seat number (1, 2, 3, ...)
        return row + String.valueOf(seatNumber);  // Combine row and seat number (e.g., A1, B2)
    }

    @Override
    public int getItemCount() {
        return seatStatusList.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        ImageView seatImage;
        TextView seatLabel;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatImage = itemView.findViewById(R.id.seat_image);
            seatLabel = itemView.findViewById(R.id.seat_label);
        }
    }

    private String getSeatDescription(int status) {
        if (status == R.drawable.available_seat) {
            return "Available seat";
        } else if (status == R.drawable.reserved_seat) {
            return "Reserved seat";
        } else {
            return "Selected seat";
        }
    }

}