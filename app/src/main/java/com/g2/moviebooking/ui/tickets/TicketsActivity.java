package com.g2.moviebooking.ui.tickets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.ui.MovieListActivity;
import com.g2.moviebooking.utils.FirebaseClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TicketsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TicketAdapter ticketAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        db = FirebaseClient.getFirestore();
        auth = FirebaseClient.getAuth();

        setupRecyclerView();
        setupBottomNavigation(); // Thêm Bottom Navigation
        fetchUserTickets();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_tickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ticketAdapter = new TicketAdapter(new ArrayList<>(), booking -> {
            Intent intent = new Intent(this, TicketDetailActivity.class);
            intent.putExtra("BOOKING", booking);
            startActivity(intent);
        });
        recyclerView.setAdapter(ticketAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_movies) {
                startActivity(new Intent(this, MovieListActivity.class));
                finish(); // Kết thúc TicketsActivity để tránh chồng activity
                return true;
            } else if (itemId == R.id.nav_theatres) {
                // TODO: Implement TheatresActivity
                showToast("Chức năng rạp chưa được triển khai");
                return true;
            } else if (itemId == R.id.nav_tickets) {
                // Đã ở TicketsActivity, không cần chuyển
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Implement ProfileActivity
                showToast("Chức năng tài khoản chưa được triển khai");
                return true;
            }

            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_tickets); // Đánh dấu Tickets là mục hiện tại
    }

    private void fetchUserTickets() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            showToast("Vui lòng đăng nhập để xem vé");
            finish();
            return;
        }

        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Booking booking = document.toObject(Booking.class);
                        booking.setId(document.getId());
                        bookings.add(booking);
                    }
                    ticketAdapter.updateTickets(bookings);
                })
                .addOnFailureListener(e -> showToast("Lỗi khi tải vé: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}