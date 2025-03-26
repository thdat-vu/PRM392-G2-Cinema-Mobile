package com.g2.moviebooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;
import com.g2.moviebooking.ui.MapsActivity;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TheatreAdapter extends RecyclerView.Adapter<TheatreAdapter.TheatreViewHolder> {

    private List<Theatre> theatres;
    private List<Showtime> showtimes;
    private Context context;
    private double userLatitude;
    private double userLongitude;


    public TheatreAdapter(List<Theatre> theatres, List<Showtime> showtimes, Context context, double userLatitude, double userLongitude) {
        this.theatres = theatres;
        this.context = context;
        this.showtimes = showtimes;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;

        if (userLatitude != 0 && userLongitude != 0) {
            // Calculate and store the distance for each theatre once
            for (Theatre theatre : this.theatres) {
                float distance = calculateDistanceForTheatre(theatre);
                theatre.setDistance(distance); // Make sure Theatre has setDistance() and getDistance()
            }
            // Sort theatres based on the computed distance
            Collections.sort(this.theatres, (t1, t2) -> Float.compare(t1.getDistance(), t2.getDistance()));
        }
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

        // Use the precomputed distance stored in the theatre object.
        float distanceMeters = theatre.getDistance();
        holder.tvDistance.setText(formatDistance(distanceMeters));

        // Toggle "Near You" indicator if within a threshold (e.g., 500 m).
        if (distanceMeters >= 0 && distanceMeters < 500) {
            holder.tvNearYou.setVisibility(View.VISIBLE);
        } else {
            holder.tvNearYou.setVisibility(View.GONE);
        }

        String format = showtimes.stream().findFirst().map(showtime -> showtime.getFormat()).get();
        if (format != null){
            holder.tvFormatLabel.setText(format);
        }
        else {
            // Example of setting format text if you want
            holder.tvFormatLabel.setText("2D Phụ đề");
        }


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
        // Set up the button to show theatre location on map
        holder.btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapsActivity.class);
            // Pass the theatre location details
            intent.putExtra("theatre_lat", theatre.getLatitude());
            intent.putExtra("theatre_lng", theatre.getLongitude());
            intent.putExtra("theatre_name", theatre.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return theatres.size();
    }

    /**
     * Calculates the distance (in meters) between the current user location and the theatre.
     * Returns -1 if the calculation cannot be performed.
     */
    private float calculateDistanceForTheatre(Theatre theatre) {
        if (userLatitude != 0 && userLongitude != 0
                && theatre.getLatitude() != 0 && theatre.getLongitude() != 0) {
            Log.d("DistanceCalc", "User: " + userLatitude + ", " + userLongitude + "; Theatre: " + theatre.getLatitude() + ", " + theatre.getLongitude());
            float[] results = new float[1];
            Location.distanceBetween(userLatitude, userLongitude, theatre.getLatitude(), theatre.getLongitude(), results);
            Log.d("DistanceCalc", "Calculated distance (meters): " + results[0]);
            return results[0];
        }
        return -1;
    }

    /**
     * Formats the distance from meters to a readable string.
     * Returns a string in meters if less than 1000 m, otherwise in kilometers.
     */
    private String formatDistance(float distanceMeters) {
        if (distanceMeters < 0) {
            return "Distance unavailable";
        } else if (distanceMeters < 1000) {
            return String.format(Locale.getDefault(), "%.0f m", distanceMeters);
        } else {
            return String.format(Locale.getDefault(), "%.2f km", distanceMeters / 1000f);
        }
    }


    class TheatreViewHolder extends RecyclerView.ViewHolder {
        TextView tvTheatreName, tvDistance, tvNearYou, tvFormatLabel;
        RecyclerView rvShowtimes;
        Button btnShowMap;

        public TheatreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTheatreName = itemView.findViewById(R.id.tvTheatreName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvNearYou = itemView.findViewById(R.id.tvNearYou);
            tvFormatLabel = itemView.findViewById(R.id.tvFormatLabel);
            rvShowtimes = itemView.findViewById(R.id.rvShowtimes);
            btnShowMap = itemView.findViewById(R.id.btnShowMap);
        }
    }
}

