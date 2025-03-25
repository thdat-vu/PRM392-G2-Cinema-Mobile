package com.g2.moviebooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TheatreAdapter extends RecyclerView.Adapter<TheatreAdapter.TheatreViewHolder> {

    private List<Theatre> theatres;
    private List<Showtime> showtimes;
    private Context context;

    public TheatreAdapter(List<Theatre> theatres,List<Showtime> showtimes, Context context) {
        this.theatres = theatres;
        this.context = context;
        this.showtimes = showtimes;
    }

    @NonNull
    @Override
    public TheatreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_theatre, parent, false);
        return new TheatreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheatreViewHolder holder, int position) {
        Theatre theatre = theatres.get(position);

        holder.tvTheatreName.setText(theatre.getName());
        /* TODO:

        holder.tvDistance.setText(theatre.getDistance());
        if (theatre.isNearYou()) {
            holder.tvNearYou.setVisibility(View.VISIBLE);
        } else {
            holder.tvNearYou.setVisibility(View.GONE);
        }*/

        // Example of setting format text if you want
        holder.tvFormatLabel.setText("2D Phụ đề");

        // Set up the inner RecyclerView with showtimes
        ShowtimesAdapter showtimesAdapter = new ShowtimesAdapter(
                showtimes.stream()
                        .filter(showtime -> showtime.getTheatreId().equals(theatre.getId()))
                        .collect(Collectors.toList()),
                context);
        holder.rvShowtimes.setAdapter(showtimesAdapter);

        // For multiple columns of time, use GridLayoutManager
        holder.rvShowtimes.setLayoutManager(new GridLayoutManager(context, 3));

        // If you want them in a row that scrolls horizontally:
        // holder.rvShowtimes.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public int getItemCount() {
        return theatres.size();
    }

    class TheatreViewHolder extends RecyclerView.ViewHolder {
        TextView tvTheatreName, tvDistance, tvNearYou, tvFormatLabel;
        RecyclerView rvShowtimes;

        public TheatreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTheatreName = itemView.findViewById(R.id.tvTheatreName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvNearYou = itemView.findViewById(R.id.tvNearYou);
            tvFormatLabel = itemView.findViewById(R.id.tvFormatLabel);
            rvShowtimes = itemView.findViewById(R.id.rvShowtimes);
        }
    }
}

