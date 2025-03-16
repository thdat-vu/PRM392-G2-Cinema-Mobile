package com.g2.moviebooking.ui.tickets;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {
    private TextView tvMovieTitle, tvTheatreName, tvShowtime, tvSeats, tvBookingCode, tvTotalAmount, tvFoodItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        setupViews();

        Booking booking = (Booking) getIntent().getSerializableExtra("BOOKING");
        if (booking != null) {
            displayTicketDetail(booking);
        } else {
            finish();
        }
    }

    private void setupViews() {
        tvMovieTitle = findViewById(R.id.ticket_detail_movie_title);
        tvTheatreName = findViewById(R.id.ticket_detail_theatre_name);
        tvShowtime = findViewById(R.id.ticket_detail_showtime);
        tvSeats = findViewById(R.id.ticket_detail_seats);
        tvBookingCode = findViewById(R.id.ticket_detail_booking_code);
        tvTotalAmount = findViewById(R.id.ticket_detail_total_amount);
        tvFoodItems = findViewById(R.id.ticket_detail_food_items);
    }

    private void displayTicketDetail(Booking booking) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Locale vnLocale = new Locale("vi", "VN");

        String movieTitle = (booking.getShowtime() != null && booking.getShowtime().getMovie() != null)
                ? booking.getShowtime().getMovie().getTitle() : "Không xác định";
        String theatreName = (booking.getShowtime() != null && booking.getShowtime().getTheatre() != null)
                ? booking.getShowtime().getTheatre().getName() : "Không xác định";
        String showtimeText = (booking.getShowtime() != null && booking.getShowtime().getStartTime() != null)
                ? dateFormat.format(booking.getShowtime().getStartTime()) : "Không xác định";
        String seatsText = (booking.getSeats() != null) ? String.join(", ", booking.getSeats()) : "Không có ghế";
        String bookingCodeText = (booking.getBookingCode() != null) ? booking.getBookingCode() : "Không có mã";

        tvMovieTitle.setText(movieTitle);
        tvTheatreName.setText(theatreName);
        tvShowtime.setText(showtimeText);
        tvSeats.setText(seatsText);
        tvBookingCode.setText(bookingCodeText);
        tvTotalAmount.setText(String.format(vnLocale, "%,d VNĐ", (long) booking.getTotalAmount()));

        StringBuilder foodItemsText = new StringBuilder();
        if (booking.getFoodItems() != null && !booking.getFoodItems().isEmpty()) {
            for (Booking.FoodItem item : booking.getFoodItems()) {
                foodItemsText.append(String.format(vnLocale, "%s (x%d): %,d VNĐ\n",
                        item.getName() != null ? item.getName() : "Không xác định",
                        item.getQuantity(),
                        (long) item.getPrice()));
            }
        } else {
            foodItemsText.append("Không có đồ ăn");
        }
        tvFoodItems.setText(foodItemsText.toString());
    }
}