package com.g2.moviebooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.g2.moviebooking.R;

import java.util.List;

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
        holder.imageView.setImageResource(status);
        holder.imageView.setContentDescription(getSeatDescription(status));
    }

    @Override
    public int getItemCount() {
        return seatStatusList.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.seat_image);
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