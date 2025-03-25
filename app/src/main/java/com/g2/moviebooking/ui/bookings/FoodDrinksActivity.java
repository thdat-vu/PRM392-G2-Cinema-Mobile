package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.adapter.FoodDrinkAdapter;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.model.FoodDrink;
import com.g2.moviebooking.data.repository.FoodAndDrinkRepository;
import com.g2.moviebooking.ui.payment.PaymentActivity;
import com.g2.moviebooking.utils.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.g2.moviebooking.data.model.Booking.FoodItem;

public class FoodDrinksActivity extends AppCompatActivity implements FoodDrinkAdapter.OnQuantityChangeListener {

    private static final String TAG = "FoodDrinksActivity";
    private RecyclerView rvFoodDrinks;
    private TextView tvTotalAmount;
    private Button btnContinue;
    private ImageButton btnBack, btnClose;
    
    private List<FoodDrink> foodDrinkList;
    private FoodDrinkAdapter adapter;
    private FoodAndDrinkRepository foodAndDrinkRepository;
    
    // Data to pass to next activity
    private String movieId;
    private Showtime showtime;
    private String[] selectedSeats;
    private double ticketPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_drinks);
        
        // Initialize repository
        foodAndDrinkRepository = new FoodAndDrinkRepository();
        
        // Initialize views
        rvFoodDrinks = findViewById(R.id.rvFoodDrinks);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);
        btnClose = findViewById(R.id.btnClose);
        
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            movieId = intent.getStringExtra("movieId");
            showtime = (Showtime) intent.getSerializableExtra(Constants.EXTRA_SHOWTIME);
            selectedSeats = intent.getStringArrayExtra(Constants.EXTRA_SELECTED_SEATS);
            ticketPrice = intent.getDoubleExtra(Constants.EXTRA_TOTAL_AMOUNT, 0);
        }
        
        // Set up RecyclerView
        rvFoodDrinks.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize food and drink list
        foodDrinkList = new ArrayList<>();
        
        // Set up adapter with empty list initially
        adapter = new FoodDrinkAdapter(this, foodDrinkList, this);
        rvFoodDrinks.setAdapter(adapter);
        
        // Fetch food and drink items
        initFoodDrinkList();
        
        // Set up click listeners
        btnBack.setOnClickListener(v -> onBackPressed());
        
        btnClose.setOnClickListener(v -> finish());
        
        btnContinue.setOnClickListener(v -> {
            // Calculate total amount
            double totalAmount = calculateTotalAmount();
            
            // Create a list of selected food and drinks
            List<FoodDrink> selectedItems = new ArrayList<>();
            for (FoodDrink item : foodDrinkList) {
                if (item.getQuantity() > 0) {
                    selectedItems.add(item);
                }
            }
            
            // Create intent for payment details activity
            Intent paymentIntent = new Intent(FoodDrinksActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("movieId", movieId);
            paymentIntent.putExtra(Constants.EXTRA_SHOWTIME, showtime);
            paymentIntent.putExtra(Constants.EXTRA_SELECTED_SEATS, selectedSeats);
            paymentIntent.putExtra(Constants.EXTRA_SEAT_PRICE, ticketPrice);
            paymentIntent.putExtra(Constants.EXTRA_FOOD_DRINKS_PRICE, totalAmount);

            if (!selectedItems.isEmpty()){
                paymentIntent.putExtra(Constants.EXTRA_FOOD_DRINKS_ITEMS, (ArrayList<FoodDrink>) selectedItems);
            }
            startActivity(paymentIntent);
        });
        
        // Update total amount initially
        updateTotalAmount();
    }
    
    private void initFoodDrinkList() {
        // Show loading state if needed
        
        foodAndDrinkRepository.getAllFoodAndDrinks()
            .thenAccept(foodItems -> {
                // Update UI on the main thread
                runOnUiThread(() -> {
                    foodDrinkList.clear();
                    
                    // Convert FoodItem to FoodDrink
                    for (FoodItem foodItem : foodItems) {
                        FoodDrink foodDrink = new FoodDrink(
                            foodItem.getId(),
                            foodItem.getName(),
                            foodItem.getPrice(),
                            "" // Empty image URL for now
                        );
                        foodDrink.setQuantity(foodItem.getQuantity());
                        foodDrinkList.add(foodDrink);
                    }
                    
                    adapter.notifyDataSetChanged();
                    updateTotalAmount();
                    Log.d(TAG, "Loaded " + foodItems.size() + " food and drink items");
                });
            })
            .exceptionally(throwable -> {
                // Handle error on the main thread
                runOnUiThread(() -> {
                    Toast.makeText(FoodDrinksActivity.this, 
                        "Lỗi khi tải danh sách đồ ăn: " + throwable.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading food and drinks: " + throwable.getMessage());
                    
                    // Load fallback data in case of error
                    loadFallbackFoodDrinkData();
                });
                return null;
            });
    }
    
    private void loadFallbackFoodDrinkData() {
        // Fallback to hardcoded data in case the repository fails
        foodDrinkList.clear();
        foodDrinkList.add(new FoodDrink("1", "iCombo 2 Big STD", 109000, ""));
        foodDrinkList.add(new FoodDrink("2", "iCombo 1 Big STD", 89000, ""));
        foodDrinkList.add(new FoodDrink("3", "iCombo 1 Big Extra STD", 109000, ""));
        foodDrinkList.add(new FoodDrink("4", "iCombo 2 Big Extra STD", 129000, ""));
        adapter.notifyDataSetChanged();
        updateTotalAmount();
    }
    
    private double calculateTotalAmount() {
        double total = 0;
        for (FoodDrink item : foodDrinkList) {
            total += item.getTotalPrice();
        }
        return total;
    }
    
    private void updateTotalAmount() {
        double total = calculateTotalAmount();
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        tvTotalAmount.setText(currencyFormat.format(total) + "đ");
    }
    
    @Override
    public void onQuantityChanged(List<FoodDrink> foodDrinkList) {
        updateTotalAmount();
    }
}