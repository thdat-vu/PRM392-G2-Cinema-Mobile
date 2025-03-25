package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.adapter.SeatAdapter;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.repository.ShowtimeRepository;
import com.g2.moviebooking.utils.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeatActivity extends AppCompatActivity {

    private RecyclerView seatRecyclerView;
    private SeatAdapter seatAdapter;
    private List<Integer> seatStatusList;
    private Button btnBuy;
    private double seatPrice = Constants.SEAT_PRICE; // Using constant instead of magic number
    private List<String> selectedSeats = new ArrayList<>();
    private double totalAmount = Constants.BASE_TOTAL_AMOUNT;
    private NumberFormat currencyFormatter;
    private Showtime showtime;
    private TextView tvTempPrice;
    private ShowtimeRepository showtimeRepository;
    private boolean isDataLoaded = false; // Biến kiểm tra dữ liệu đã tải xong chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats_booking);
        showtime = (Showtime) getIntent().getSerializableExtra(Constants.EXTRA_SHOWTIME);

        // Initialize currency formatter for Vietnamese Dong
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Initialize repository
        showtimeRepository = new ShowtimeRepository();

        // Initialize views
        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        btnBuy = findViewById(R.id.btnBuy);
        tvTempPrice = findViewById(R.id.tvTempPrice);

        // Set initial button text (nút sẽ bị vô hiệu hóa vì chưa tải dữ liệu)
        updateBuyButtonText();

        // Initialize seat status list and fetch booked seats
        initializeSeatStatusList();

        // Set up seat adapter with seat selection listener
        setupSeatAdapter();

        // Set up buy button click listener
        setupBuyButton();
    }

    private void initializeSeatStatusList() {
        seatStatusList = new ArrayList<>();
        // Khởi tạo tất cả ghế là available trước
        for (int i = 0; i < 64; i++) {
            seatStatusList.add(R.drawable.available_seat); // Available seats
        }

        // Vô hiệu hóa nút "Mua" cho đến khi dữ liệu được tải
        btnBuy.setEnabled(false);

        // Lấy dữ liệu từ Firestore nếu có showtime
        if (showtime != null && showtime.getId() != null) {
            showtimeRepository.getShowtimeDetail(showtime.getId(), new ShowtimeRepository.ShowtimeCallback<Showtime>() {
                @Override
                public void onSuccess(Showtime updatedShowtime) {
                    // Cập nhật showtime với dữ liệu mới nhất
                    showtime = updatedShowtime;

                    // Cập nhật trạng thái ghế đã booked
                    List<String> bookedSeats = showtime.getBookedSeats();
                    if (bookedSeats != null && !bookedSeats.isEmpty()) {
                        for (String bookedSeat : bookedSeats) {
                            int position = getPositionFromSeatLabel(bookedSeat);
                            if (position >= 0 && position < seatStatusList.size()) {
                                seatStatusList.set(position, R.drawable.reserved_seat);
                            }
                        }
                    }

                    // Cập nhật adapter sau khi tải dữ liệu
                    if (seatAdapter != null) {
                        seatAdapter.notifyDataSetChanged();
                    }

                    // Đánh dấu dữ liệu đã tải xong và cập nhật trạng thái nút
                    isDataLoaded = true;
                    updateBuyButtonText();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(SeatActivity.this, "Lỗi khi tải dữ liệu ghế: " + error, Toast.LENGTH_SHORT).show();
                    // Nếu lỗi, vẫn cho phép tiếp tục với dữ liệu mặc định
                    isDataLoaded = true;
                    updateBuyButtonText();
                }
            });
        } else {
            // Nếu không có showtime hợp lệ, vẫn cho phép tiếp tục với dữ liệu mặc định
            isDataLoaded = true;
            updateBuyButtonText();
        }
    }

    private int getPositionFromSeatLabel(String seatLabel) {
        // Chuyển đổi seatLabel (e.g., "A1") thành position trong danh sách
        if (seatLabel == null || seatLabel.length() < 2) return -1;
        char row = seatLabel.charAt(0); // Lấy ký tự hàng (A, B, C, ...)
        int seatNumber;
        try {
            seatNumber = Integer.parseInt(seatLabel.substring(1)); // Lấy số ghế (1, 2, 3, ...)
        } catch (NumberFormatException e) {
            return -1;
        }
        int rowIndex = row - 'A'; // Chuyển đổi hàng thành chỉ số (A=0, B=1, ...)
        return rowIndex * 8 + (seatNumber - 1); // Tính vị trí trong danh sách 64 ghế
    }

    private void setupSeatAdapter() {
        seatAdapter = new SeatAdapter(seatStatusList);
        seatRecyclerView.setLayoutManager(new GridLayoutManager(this, 8));
        seatRecyclerView.setAdapter(seatAdapter);

        // Set seat selection listener
        seatAdapter.setOnSeatSelectedListener(new SeatAdapter.OnSeatSelectedListener() {
            @Override
            public void onSeatSelected(int position, boolean isSelected, String seatLabel) {
                if (isSelected) {
                    selectedSeats.add(seatLabel);
                    totalAmount += seatPrice;
                } else {
                    selectedSeats.remove(seatLabel);
                    totalAmount -= seatPrice;
                }
                updateBuyButtonText();
            }
        });
    }

    private void updateBuyButtonText() {
        String formattedAmount = currencyFormatter.format(totalAmount);
        tvTempPrice.setText(formattedAmount);

        // Chỉ bật nút khi dữ liệu đã tải xong và có ghế được chọn
        btnBuy.setEnabled(isDataLoaded && totalAmount > 0);
    }

    private void setupBuyButton() {
        btnBuy.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create intent to FoodDrinksActivity
            Intent intent = new Intent(SeatActivity.this, FoodDrinksActivity.class);

            // Pass selected seats and total amount using constants for keys
            intent.putExtra(Constants.EXTRA_TOTAL_AMOUNT, totalAmount);
            intent.putExtra(Constants.EXTRA_SELECTED_SEATS, selectedSeats.toArray(new String[0]));
            intent.putExtra(Constants.EXTRA_SHOWTIME, showtime);

            startActivity(intent);
        });
    }
}