package com.g2.moviebooking.ui.tickets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;
import com.g2.moviebooking.data.repository.BookingRepository;
import com.g2.moviebooking.data.repository.FoodAndDrinkRepository;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.data.repository.ShowtimeRepository;
import com.g2.moviebooking.data.repository.TheatreRepository;
import com.g2.moviebooking.ui.MovieListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketsActivity extends AppCompatActivity {
    private static final String TAG = "TicketsActivity";
    private RecyclerView recyclerView;
    private TicketAdapter ticketAdapter;
    private BookingRepository bookingRepository;
    private ShowtimeRepository showtimeRepository;
    private TheatreRepository theatreRepository;
    private MovieRepository movieRepository;
    private FoodAndDrinkRepository foodRepo;
    private FirebaseAuth auth;
    private Map<String, Theatre> theatreMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        auth = FirebaseAuth.getInstance();
        bookingRepository = new BookingRepository();
        showtimeRepository = new ShowtimeRepository();
        theatreRepository = new TheatreRepository();
        movieRepository = new MovieRepository(this);
        foodRepo = new FoodAndDrinkRepository();
        theatreMap = new HashMap<>();

        setupRecyclerView();
        setupBottomNavigation();
        loadTheatres();
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
                finish();
                return true;
            } else if (itemId == R.id.nav_theatres) {
                showToast("Chức năng rạp chưa được triển khai");
                return true;
            } else if (itemId == R.id.nav_tickets) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                showToast("Chức năng tài khoản chưa được triển khai");
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_tickets);
    }

    private void loadTheatres() {
        theatreRepository.getAllTheatres(new TheatreRepository.TheatreCallback<List<Theatre>>() {
            @Override
            public void onSuccess(List<Theatre> theatres) {
                for (Theatre theatre : theatres) {
                    theatreMap.put(theatre.getId(), theatre);
                }
                Log.d(TAG, "Loaded " + theatreMap.size() + " theatres");
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to load theatres: " + error);
                showToast("Lỗi khi tải danh sách rạp: " + error);
            }
        });
    }

    private void fetchUserTickets() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            showToast("Vui lòng đăng nhập để xem vé");
            finish();
            return;
        }
        String userId = currentUser.getUid();

        bookingRepository.getBookingsByUserId(userId, new BookingRepository.BookingCallback<List<Booking>>() {
            @Override
            public void onSuccess(List<Booking> bookings) {
                Log.d(TAG, "Fetched " + bookings.size() + " bookings");
                fetchShowtimeDetails(bookings);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to fetch bookings: " + error);
                showToast("Lỗi khi tải vé: " + error);
            }
        });
    }

    private void fetchShowtimeDetails(List<Booking> bookings) {
        List<Booking> updatedBookings = new ArrayList<>(bookings);
        int[] completedCount = {0};

        if (bookings.isEmpty()) {
            ticketAdapter.updateTickets(updatedBookings);
            return;
        }

        for (Booking booking : updatedBookings) {
            showtimeRepository.getShowtimeDetail(booking.getShowtimeId(), new ShowtimeRepository.ShowtimeCallback<Showtime>() {
                @Override
                public void onSuccess(Showtime showtime) {
                    booking.setShowtime(showtime);
                    Theatre theatre = theatreMap.get(showtime.getTheatreId());
                    if (theatre != null) {
                        showtime.setTheatre(theatre);
                    } else {
                        Log.w(TAG, "Theatre not found for theatreId: " + showtime.getTheatreId());
                    }
                    fetchMovieDetails(showtime, booking, updatedBookings, completedCount);
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Failed to fetch showtime " + booking.getShowtimeId() + ": " + error);
                    synchronized (completedCount) {
                        completedCount[0]++;
                        if (completedCount[0] == bookings.size()) {
                            ticketAdapter.updateTickets(updatedBookings);
                        }
                    }
                }
            });
        }
    }

    private void fetchMovieDetails(Showtime showtime, Booking booking, List<Booking> updatedBookings, int[] completedCount) {
        movieRepository.getMovieDetail(showtime.getMovieId(), new MovieRepository.MovieCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                showtime.setMovie(movie);
                fetchFoodItems(booking, updatedBookings, completedCount);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to fetch movie " + showtime.getMovieId() + ": " + error);
                fetchFoodItems(booking, updatedBookings, completedCount); // Vẫn lấy foodItems dù lỗi movie
            }
        });
    }

    private void fetchFoodItems(Booking booking, List<Booking> updatedBookings, int[] completedCount) {
        if (booking.getFoodItems() != null && !booking.getFoodItems().isEmpty()) {
            foodRepo.getFoodItemsForBooking(booking.getFoodItems())
                    .thenAccept(foodItems -> {
                        booking.setFoodItems(foodItems);
                        synchronized (completedCount) {
                            completedCount[0]++;
                            if (completedCount[0] == updatedBookings.size()) {
                                ticketAdapter.updateTickets(updatedBookings);
                            }
                        }
                    })
                    .exceptionally(throwable -> {
                        Log.e(TAG, "Failed to fetch food items for booking " + booking.getId() + ": " + throwable.getMessage());
                        synchronized (completedCount) {
                            completedCount[0]++;
                            if (completedCount[0] == updatedBookings.size()) {
                                ticketAdapter.updateTickets(updatedBookings);
                            }
                        }
                        return null;
                    });
        } else {
            synchronized (completedCount) {
                completedCount[0]++;
                if (completedCount[0] == updatedBookings.size()) {
                    ticketAdapter.updateTickets(updatedBookings);
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}