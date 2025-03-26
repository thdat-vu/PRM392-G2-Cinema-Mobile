package com.g2.moviebooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.ui.bookings.SeatActivity;
import com.g2.moviebooking.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ShowtimesAdapter extends RecyclerView.Adapter<ShowtimesAdapter.ShowtimeViewHolder> {

    private List<Showtime> showtimes;
    private Context context;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ShowtimesAdapter(List<Showtime> showtimes, Context context) {
        this.showtimes = showtimes;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimes.get(position);

        // Format the time (e.g., "09:10")
        String startTimeText = timeFormat.format(showtime.getStartTime());
        holder.btnTime.setText(startTimeText);

        // You can also append format/language if you want:
        // holder.btnTime.setText(startTimeText + " - " + showtime.getFormat() + " - " + showtime.getLanguage());

        holder.btnTime.setOnClickListener(v -> {
            // Navigate to seat selection
            Intent intent = new Intent(context, SeatActivity.class);
            // pass data in the intent
            intent.putExtra(Constants.EXTRA_SHOWTIME, showtime);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return showtimes.size();
    }

    class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        Button btnTime;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTime = itemView.findViewById(R.id.btnTime);
        }
    }
}

