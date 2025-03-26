package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.data.repository.ShowtimeRepository;
import com.g2.moviebooking.data.repository.TheatreRepository;
import com.g2.moviebooking.ui.MovieListActivity;
import com.g2.moviebooking.ui.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryListActivity extends AppCompatActivity {

    private static final String TAG = "BookingHistoryList";
    private RecyclerView recyclerViewBookings;
    private BookingHistoryAdapter adapter;
    private List<Booking> bookingList;
    private LinearLayout emptyStateLayout;
    private ImageButton btnBack, btnSupport, btnClose;
    private BookingRepository bookingRepository;
    private ShowtimeRepository showtimeRepository;
    private MovieRepository movieRepository;
    private TheatreRepository theatreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history_list);
        Log.d(TAG, "onCreate: Khởi tạo BookingHistoryListActivity");

        // Khởi tạo các repository
        bookingRepository = new BookingRepository();
        showtimeRepository = new ShowtimeRepository();
        movieRepository = new MovieRepository();
        theatreRepository = new TheatreRepository();
        Log.d(TAG, "onCreate: Đã khởi tạo các Repository");

        // Khởi tạo views
        initViews();
        Log.d(TAG, "onCreate: Đã khởi tạo views");

        // Thiết lập RecyclerView
        setupRecyclerView();
        Log.d(TAG, "onCreate: Đã thiết lập RecyclerView");

        // Tải dữ liệu bookings từ API
        loadBookings();
        Log.d(TAG, "onCreate: Bắt đầu tải dữ liệu bookings");

        // Thiết lập bottom navigation
        setupBottomNavigation();
        Log.d(TAG, "onCreate: Đã thiết lập bottom navigation");
    }

    private void initViews() {
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBack = findViewById(R.id.btnBack);
        btnSupport = findViewById(R.id.btnSupport);
        btnClose = findViewById(R.id.btnClose);
        Log.d(TAG, "initViews: Đã tìm thấy các view");
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(bookingList, this::onBookingItemClick);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookings.setAdapter(adapter);
        Log.d(TAG, "setupRecyclerView: Đã thiết lập RecyclerView với adapter");
    }

    private void loadBookings() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        Log.d(TAG, "loadBookings: userId = " + userId);

        if (userId != null) {
            Log.d(TAG, "loadBookings: Đang tải bookings cho userId = " + userId);
            bookingRepository.getBookingsByUserId(userId, new BookingRepository.BookingCallback<List<Booking>>() {
                @Override
                public void onSuccess(List<Booking> bookings) {
                    Log.d(TAG, "onSuccess: Đã nhận được " + bookings.size() + " bookings");
                    bookingList.clear();
                    bookingList.addAll(bookings);

                    // Fetch Showtime, Movie, và Theatre cho từng booking
                    fetchShowtimeDetailsForBookings(bookingList, 0);
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "onFailure: Lỗi khi tải bookings - " + error);
                    Toast.makeText(BookingHistoryListActivity.this, "Failed to load bookings: " + error, Toast.LENGTH_SHORT).show();
                    updateUI();
                }
            });
        } else {
            Log.w(TAG, "loadBookings: User chưa đăng nhập");
            Toast.makeText(this, "Please log in to view bookings", Toast.LENGTH_SHORT).show();
            updateUI();
        }
    }

    private void fetchShowtimeDetailsForBookings(List<Booking> bookings, int index) {
        if (index >= bookings.size()) {
            Log.d(TAG, "fetchShowtimeDetailsForBookings: Đã fetch xong tất cả showtimes");
            updateUI();
            return;
        }

        Booking booking = bookings.get(index);
        String showtimeId = booking.getShowtimeId(); // Lấy showtimeId từ Booking
        Log.d(TAG, "fetchShowtimeDetailsForBookings: Đang fetch showtime cho booking ID = " + booking.getId() + ", showtimeId = " + showtimeId);

        if (showtimeId != null) {
            showtimeRepository.getShowtimeDetail(showtimeId, new ShowtimeRepository.ShowtimeCallback<Showtime>() {
                @Override
                public void onSuccess(Showtime showtime) {
                    Log.d(TAG, "fetchShowtimeDetailsForBookings: Đã nhận showtime ID = " + showtime.getId());
                    booking.setShowtime(showtime);

                    // Fetch Movie và Theatre cho Showtime
                    fetchMovieAndTheatreForShowtime(booking, showtime, index);
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "fetchShowtimeDetailsForBookings: Lỗi khi fetch showtime - " + error);
                    // Tiếp tục với booking tiếp theo ngay cả khi có lỗi
                    fetchShowtimeDetailsForBookings(bookings, index + 1);
                }
            });
        } else {
            Log.w(TAG, "fetchShowtimeDetailsForBookings: showtimeId is null cho booking ID = " + booking.getId());
            fetchShowtimeDetailsForBookings(bookings, index + 1);
        }
    }

    private void fetchMovieAndTheatreForShowtime(Booking booking, Showtime showtime, int index) {
        String movieId = showtime.getMovieId();
        String theatreId = showtime.getTheatreId();
        Log.d(TAG, "fetchMovieAndTheatreForShowtime: Đang fetch movieId = " + movieId + " và theatreId = " + theatreId);

        // Fetch Movie
        movieRepository.getMovieDetail(movieId, new MovieRepository.MovieCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                Log.d(TAG, "fetchMovieAndTheatreForShowtime: Đã nhận movie title = " + movie.getTitle());
                showtime.setMovie(movie);

                // Fetch Theatre
                theatreRepository.getTheatreDetail(theatreId, new TheatreRepository.TheatreCallback<Theatre>() {
                    @Override
                    public void onSuccess(Theatre theatre) {
                        Log.d(TAG, "fetchMovieAndTheatreForShowtime: Đã nhận theatre name = " + theatre.getName());
                        showtime.setTheatre(theatre);

                        // Tiếp tục với booking tiếp theo
                        fetchShowtimeDetailsForBookings(bookingList, index + 1);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "fetchMovieAndTheatreForShowtime: Lỗi khi fetch theatre - " + error);
                        fetchShowtimeDetailsForBookings(bookingList, index + 1);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "fetchMovieAndTheatreForShowtime: Lỗi khi fetch movie - " + error);
                fetchShowtimeDetailsForBookings(bookingList, index + 1);
            }
        });
    }

    private void updateUI() {
        Log.d(TAG, "updateUI: Cập nhật UI với " + bookingList.size() + " bookings");
        if (bookingList.isEmpty()) {
            recyclerViewBookings.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            Log.d(TAG, "updateUI: Hiển thị empty state");
        } else {
            recyclerViewBookings.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "updateUI: Hiển thị RecyclerView với dữ liệu");
        }
    }

    private void onBookingItemClick(Booking booking) {
        Log.d(TAG, "onBookingItemClick: Đã click vào booking ID = " + booking.getId());
        Intent intent = new Intent(this, BookingHistoryInfoActivity.class);
        intent.putExtra("booking", booking);
        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_movies) {
                Log.d(TAG, "setupBottomNavigation: Chuyển đến MovieListActivity");
                startActivity(new Intent(this, MovieListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_theatres) {
                Log.d(TAG, "setupBottomNavigation: Chức năng rạp chưa được triển khai");
                Toast.makeText(this, "Chức năng rạp chưa được triển khai", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_tickets) {
                Log.d(TAG, "setupBottomNavigation: Đang ở màn hình tickets");
                return true;
            } else if (itemId == R.id.nav_profile) {
                Log.d(TAG, "setupBottomNavigation: Chuyển đến ProfileActivity");
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_tickets);
        Log.d(TAG, "setupBottomNavigation: Đã thiết lập bottom navigation với tab tickets được chọn");
    }
}