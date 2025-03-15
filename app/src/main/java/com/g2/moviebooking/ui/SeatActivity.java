package com.g2.moviebooking.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;

import java.util.ArrayList;
import java.util.List;

public class SeatActivity extends AppCompatActivity {

    private RecyclerView seatRecyclerView;
    private SeatAdapter seatAdapter;
    private List<Integer> seatStatusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats_booking);

        // Initialize RecyclerView
        seatRecyclerView = findViewById(R.id.seatRecyclerView);

        // Generate fake data (64 seats for an 8x8 grid)
        seatStatusList = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if (i % 5 == 0) {
                seatStatusList.add(R.drawable.reserved_seat);
            } else if (i % 7 == 0) {
                seatStatusList.add(R.drawable.seat_selected);
            } else {
                seatStatusList.add(R.drawable.available_seat);
            }
        }

        seatAdapter = new SeatAdapter(seatStatusList);
        seatRecyclerView.setLayoutManager(new GridLayoutManager(this, 8)); // 8 columns
        seatRecyclerView.setAdapter(seatAdapter);
    }

}
