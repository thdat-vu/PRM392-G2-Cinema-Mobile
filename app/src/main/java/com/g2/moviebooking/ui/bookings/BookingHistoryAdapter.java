package com.g2.moviebooking.ui.bookings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private final List<Booking> bookings;
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingHistoryAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCinemaLogo;
        private final TextView tvMovieTitle;
        private final TextView tvCinemaName;
        private final TextView tvShowtimeDate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCinemaLogo = itemView.findViewById(R.id.imgCinemaLogo);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvShowtimeDate = itemView.findViewById(R.id.tvShowtimeDate);
        }

        public void bind(Booking booking, OnBookingClickListener listener) {
            Showtime showtime = booking.getShowtime();
            if (showtime != null) {
                // Set movie title
                if (showtime.getMovie() != null) {
                    tvMovieTitle.setText(showtime.getMovie().getTitle());
                }

                // Set cinema name
                Theatre theatre = showtime.getTheatre();
                if (theatre != null) {
                    tvCinemaName.setText(theatre.getName());
                    
                    // Load cinema logo
                    if (theatre.getLogoUrl() != null && !theatre.getLogoUrl().isEmpty()) {
                        // In a real app, you would load the image from a URL
                        // For this example, we'll load from drawable resources
                        int resourceId = getLogoResourceId(theatre.getLogoUrl(), itemView.getContext());
                        if (resourceId != 0) {
                            Glide.with(itemView.getContext())
                                    .load(resourceId)
                                    .circleCrop()
                                    .into(imgCinemaLogo);
                        }
                    }
                }

                // Set showtime and date
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                
                String timeStr = "";
                String dateStr = "";
                
                if (showtime.getStartTime() != null) {
                    timeStr = timeFormat.format(showtime.getStartTime());
                }
                
                if (showtime.getDate() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(showtime.getDate());
                    
                    String dayOfWeek = dayFormat.format(calendar.getTime());
                    String formattedDate = dateFormat.format(calendar.getTime());
                    
                    // Convert to Vietnamese day abbreviation
                    switch (dayOfWeek.toLowerCase()) {
                        case "mon":
                            dayOfWeek = "T2";
                            break;
                        case "tue":
                            dayOfWeek = "T3";
                            break;
                        case "wed":
                            dayOfWeek = "T4";
                            break;
                        case "thu":
                            dayOfWeek = "T5";
                            break;
                        case "fri":
                            dayOfWeek = "T6";
                            break;
                        case "sat":
                            dayOfWeek = "T7";
                            break;
                        case "sun":
                            dayOfWeek = "CN";
                            break;
                    }
                    
                    dateStr = dayOfWeek + ", " + formattedDate;
                }
                
                tvShowtimeDate.setText(timeStr + " • " + dateStr);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookingClick(booking);
                }
            });
        }

        private int getLogoResourceId(String logoName, android.content.Context context) {
            // Map logo name to drawable resource ID
            switch (logoName.toLowerCase()) {
                case "galaxy_logo":
                    return R.drawable.ic_galaxy_logo;
                case "cinestar_logo":
                    return R.drawable.ic_cinestar_logo;
                case "lotte_logo":
                    return R.drawable.ic_lotte_logo;
                default:
                    return R.drawable.ic_cinema_logo;
            }
        }
    }
}