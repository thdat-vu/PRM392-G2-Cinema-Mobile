package com.g2.moviebooking.ui.bookings;

import android.util.Log;
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

    private static final String TAG = "BookingHistoryAdapter"; // TAG cho log
    private final List<Booking> bookings;
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingHistoryAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        Log.d(TAG, "BookingHistoryAdapter: Khởi tạo adapter với " + bookings.size() + " bookings");
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        Log.d(TAG, "onCreateViewHolder: Đã inflate view cho ViewHolder");
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        Log.d(TAG, "onBindViewHolder: Binding booking ID = " + booking.getId() + " tại position " + position);
        holder.bind(booking, listener);
    }

    @Override
    public int getItemCount() {
        int count = bookings.size();
        Log.d(TAG, "getItemCount: Trả về " + count + " items");
        return count;
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
            Log.d(TAG, "BookingViewHolder: Đã tìm thấy các view trong item");
        }

        public void bind(Booking booking, OnBookingClickListener listener) {
            Showtime showtime = booking.getShowtime();
            if (showtime != null) {
                Log.d(TAG, "bind: Showtime ID = " + showtime.getId());
                // Set movie title
                if (showtime.getMovie() != null) {
                    tvMovieTitle.setText(showtime.getMovie().getTitle());
                    Log.d(TAG, "bind: Đã set tiêu đề phim = " + showtime.getMovie().getTitle());
                } else {
                    Log.w(TAG, "bind: Movie is null");
                }

                // Set cinema name and logo
                Theatre theatre = showtime.getTheatre();
                if (theatre != null) {
                    tvCinemaName.setText(theatre.getName());
                    Log.d(TAG, "bind: Đã set tên rạp = " + theatre.getName());
                    if (theatre.getLogoUrl() != null && !theatre.getLogoUrl().isEmpty()) {
                        Glide.with(itemView.getContext())
                                .load(theatre.getLogoUrl())
                                .circleCrop()
                                .into(imgCinemaLogo);
                        Log.d(TAG, "bind: Đã load logo rạp từ " + theatre.getLogoUrl());
                    } else {
                        Log.w(TAG, "bind: Logo URL is null or empty");
                    }
                } else {
                    Log.w(TAG, "bind: Theatre is null");
                }

                // Set showtime and date
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

                String timeStr = showtime.getStartTime() != null ? timeFormat.format(showtime.getStartTime()) : "N/A";
                String dateStr = "";
                if (showtime.getDate() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(showtime.getDate());
                    String dayOfWeek = dayFormat.format(calendar.getTime());
                    String formattedDate = dateFormat.format(calendar.getTime());

                    // Chuyển sang tiếng Việt
                    switch (dayOfWeek.toLowerCase()) {
                        case "mon": dayOfWeek = "T2"; break;
                        case "tue": dayOfWeek = "T3"; break;
                        case "wed": dayOfWeek = "T4"; break;
                        case "thu": dayOfWeek = "T5"; break;
                        case "fri": dayOfWeek = "T6"; break;
                        case "sat": dayOfWeek = "T7"; break;
                        case "sun": dayOfWeek = "CN"; break;
                    }
                    dateStr = dayOfWeek + ", " + formattedDate;
                }
                tvShowtimeDate.setText(timeStr + " • " + dateStr);
                Log.d(TAG, "bind: Đã set thời gian chiếu = " + timeStr + " • " + dateStr);
            } else {
                Log.w(TAG, "bind: Showtime is null");
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                Log.d(TAG, "bind: Đã click vào item booking ID = " + booking.getId());
                if (listener != null) {
                    listener.onBookingClick(booking);
                }
            });
        }
    }
}