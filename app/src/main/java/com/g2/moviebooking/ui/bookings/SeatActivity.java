package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.adapter.SeatAdapter;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.ui.payment.PaymentActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats_booking);
        showtime = (Showtime) getIntent().getSerializableExtra(Constants.EXTRA_SHOWTIME);

        // Initialize currency formatter for Vietnamese Dong
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        
        // Initialize views
        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        btnBuy = findViewById(R.id.btnBuy);
        
        // Set initial button text
        updateBuyButtonText();

        // Initialize seat status list
        initializeSeatStatusList();
        
        // Set up seat adapter with seat selection listener
        setupSeatAdapter();
        
        // Set up buy button click listener
        setupBuyButton();
    }
    
    private void initializeSeatStatusList() {
        seatStatusList = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
                seatStatusList.add(R.drawable.available_seat); // Available seats
            }
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
        btnBuy.setText("Mua " + formattedAmount);
        
        // Disable button if no seats selected
        btnBuy.setEnabled(totalAmount > 0);
    }
    
    private void setupBuyButton() {
        btnBuy.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create intent to payment activity
            Intent intent = new Intent(SeatActivity.this, FoodDrinksActivity.class);
            
            // Pass selected seats and total amount using constants for keys
            intent.putExtra(Constants.EXTRA_TOTAL_AMOUNT, totalAmount);
            intent.putExtra(Constants.EXTRA_SELECTED_SEATS, selectedSeats.toArray(new String[0]));

            if (getIntent().hasExtra(Constants.EXTRA_SHOWTIME)) {
                intent.putExtra(Constants.EXTRA_SHOWTIME, showtime);
            }
            
            startActivity(intent);
        });
    }
}
