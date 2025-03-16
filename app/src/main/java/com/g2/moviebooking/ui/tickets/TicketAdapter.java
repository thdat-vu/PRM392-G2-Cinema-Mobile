package com.g2.moviebooking.ui.tickets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<Booking> tickets;
    private final OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Booking booking);
    }

    public TicketAdapter(List<Booking> tickets, OnTicketClickListener listener) {
        this.tickets = tickets != null ? tickets : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {
        Booking booking = tickets.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Lấy thông tin từ Showtime
        String movieTitle = (booking.getShowtime() != null && booking.getShowtime().getMovie() != null)
                ? booking.getShowtime().getMovie().getTitle() : "Không xác định";
        String theatreName = (booking.getShowtime() != null && booking.getShowtime().getTheatre() != null)
                ? booking.getShowtime().getTheatre().getName() : "Không xác định";
        String showtimeText = (booking.getShowtime() != null && booking.getShowtime().getStartTime() != null)
                ? dateFormat.format(booking.getShowtime().getStartTime()) : "Không xác định";

        holder.movieTitle.setText(movieTitle);
        holder.theatreName.setText(theatreName);
        holder.showtime.setText(showtimeText);
        holder.seats.setText(booking.getSeats() != null ? String.join(", ", booking.getSeats()) : "Không có ghế");
        holder.bookingCode.setText(booking.getBookingCode() != null ? booking.getBookingCode() : "Không có mã");

        holder.itemView.setOnClickListener(v -> listener.onTicketClick(booking));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public void updateTickets(List<Booking> newTickets) {
        tickets.clear();
        tickets.addAll(newTickets != null ? newTickets : new ArrayList<>());
        notifyDataSetChanged();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle, theatreName, showtime, seats, bookingCode;

        TicketViewHolder(View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.ticket_movie_title);
            theatreName = itemView.findViewById(R.id.ticket_theatre_name);
            showtime = itemView.findViewById(R.id.ticket_showtime);
            seats = itemView.findViewById(R.id.ticket_seats);
            bookingCode = itemView.findViewById(R.id.ticket_booking_code);
        }
    }
}