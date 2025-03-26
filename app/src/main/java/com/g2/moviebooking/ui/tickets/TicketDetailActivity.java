package com.g2.moviebooking.ui.tickets;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.repository.FoodAndDrinkRepository;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {
    private TextView tvMovieTitle, tvTheatreName, tvShowtime, tvSeats, tvBookingCode, tvTotalAmount, tvFoodItems,
            tvPaymentMethod, tvPaymentStatus, tvTransactionId, tvTransactionTime, tvStatus;
    private FoodAndDrinkRepository foodRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        foodRepo = new FoodAndDrinkRepository();
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
        tvPaymentMethod = findViewById(R.id.ticket_detail_payment_method);
        tvPaymentStatus = findViewById(R.id.ticket_detail_payment_status);
        tvTransactionId = findViewById(R.id.ticket_detail_transaction_id);
        tvTransactionTime = findViewById(R.id.ticket_detail_transaction_time);
        tvStatus = findViewById(R.id.ticket_detail_status);
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
        String paymentMethodText = (booking.getPaymentMethod() != null) ? booking.getPaymentMethod() : "Không xác định";
        String paymentStatusText = (booking.getPaymentStatus() != null) ? booking.getPaymentStatus() : "Không xác định";
        String transactionIdText = (booking.getTransactionId() != null) ? booking.getTransactionId() : "Không có";
        String transactionTimeText = (booking.getTransactionTime() != null)
                ? dateFormat.format(booking.getTransactionTime()) : "Không có";
        String statusText = (booking.getStatus() != null) ? booking.getStatus() : "Không xác định";

        tvMovieTitle.setText(movieTitle);
        tvTheatreName.setText(theatreName);
        tvShowtime.setText(showtimeText);
        tvSeats.setText(seatsText);
        tvBookingCode.setText(bookingCodeText);
        tvTotalAmount.setText(String.format(vnLocale, "%,d VNĐ", (long) booking.getTotalAmount()));
        tvPaymentMethod.setText(paymentMethodText);
        tvPaymentStatus.setText(paymentStatusText);
        tvTransactionId.setText(transactionIdText);
        tvTransactionTime.setText(transactionTimeText);
        tvStatus.setText(statusText);

        // Lấy thông tin foodItems từ FoodAndDrinkRepository
        if (booking.getFoodItems() != null && !booking.getFoodItems().isEmpty()) {
            foodRepo.getFoodItemsForBooking(booking.getFoodItems())
                    .thenAccept(foodItems -> {
                        StringBuilder foodItemsText = new StringBuilder();
                        for (Booking.FoodItem item : foodItems) {
                            foodItemsText.append(String.format(vnLocale, "%s (x%d): %,d VNĐ\n",
                                    item.getName() != null ? item.getName() : "Không xác định",
                                    item.getQuantity(),
                                    (long) (item.getPrice() * item.getQuantity())));
                        }
                        runOnUiThread(() -> tvFoodItems.setText(foodItemsText.toString()));
                    })
                    .exceptionally(throwable -> {
                        runOnUiThread(() -> tvFoodItems.setText("Lỗi khi tải đồ ăn: " + throwable.getMessage()));
                        return null;
                    });
        } else {
            tvFoodItems.setText("Không có đồ ăn");
        }
    }
}