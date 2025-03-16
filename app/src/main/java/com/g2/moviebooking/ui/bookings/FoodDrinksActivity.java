package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.adapter.FoodDrinkAdapter;
import com.g2.moviebooking.model.FoodDrink;
import com.g2.moviebooking.ui.payment.PaymentActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodDrinksActivity extends AppCompatActivity implements FoodDrinkAdapter.OnQuantityChangeListener {

    private RecyclerView rvFoodDrinks;
    private TextView tvTotalAmount;
    private Button btnContinue;
    private ImageButton btnBack, btnClose;
    
    private List<FoodDrink> foodDrinkList;
    private FoodDrinkAdapter adapter;
    
    // Data to pass to next activity
    private String movieId;
    private String showTimeId;
    private String selectedSeats;
    private double ticketPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_drinks);
        
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
            showTimeId = intent.getStringExtra("showTimeId");
            selectedSeats = intent.getStringExtra("selectedSeats");
            ticketPrice = intent.getDoubleExtra("ticketPrice", 0);
        }
        
        // Set up RecyclerView
        rvFoodDrinks.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize food and drink list
        initFoodDrinkList();
        
        // Set up adapter
        adapter = new FoodDrinkAdapter(this, foodDrinkList, this);
        rvFoodDrinks.setAdapter(adapter);
        
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
            paymentIntent.putExtra("showTimeId", showTimeId);
            paymentIntent.putExtra("selectedSeats", selectedSeats);
            paymentIntent.putExtra("ticketPrice", ticketPrice);
            paymentIntent.putExtra("foodDrinkPrice", totalAmount);
            
            // Convert selected items to string format to pass to next activity
            StringBuilder foodDrinkDetails = new StringBuilder();
            for (FoodDrink item : selectedItems) {
                foodDrinkDetails.append(item.getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(", ");
            }
            
            if (foodDrinkDetails.length() > 0) {
                foodDrinkDetails.delete(foodDrinkDetails.length() - 2, foodDrinkDetails.length());
                paymentIntent.putExtra("foodDrinkDetails", foodDrinkDetails.toString());
            }
            
            startActivity(paymentIntent);
        });
        
        // Update total amount initially
        updateTotalAmount();
    }
    
    // TODO: Fetch API 
    private void initFoodDrinkList() {
        foodDrinkList = new ArrayList<>();
        
        // Add sample food and drink items
        foodDrinkList.add(new FoodDrink("1", "iCombo 2 Big STD", 109000, ""));
        foodDrinkList.add(new FoodDrink("2", "iCombo 1 Big STD", 89000, ""));
        foodDrinkList.add(new FoodDrink("3", "iCombo 1 Big Extra STD", 109000, ""));
        foodDrinkList.add(new FoodDrink("4", "iCombo 2 Big Extra STD", 129000, ""));
        
        // In a real app, you would fetch this data from a database or API
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