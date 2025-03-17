package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.os.Bundle;
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
import com.g2.moviebooking.ui.MovieListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingHistoryListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBookings;
    private BookingHistoryAdapter adapter;
    private List<Booking> bookingList;
    private LinearLayout emptyStateLayout;
    private TabLayout tabLayout;
    private FloatingActionButton fabFilter;
    private ImageButton btnBack, btnSupport, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history_list);

        // Initialize views
        initViews();
        
        // Set up recycler view - moved before tab layout setup
        setupRecyclerView();
        
        // Load bookings - moved before tab layout setup
        loadBookings();
        
        // Set up tab layout - moved after loading data
        // setupTabLayout();
        
        // Set up click listeners
        // setupClickListeners();

        // Bottomtabs
        setupBottomNavigation();
    }

    private void initViews() {
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnBack = findViewById(R.id.btnBack);
        btnSupport = findViewById(R.id.btnSupport);
        btnClose = findViewById(R.id.btnClose);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Filter bookings based on selected tab
                filterBookingsByTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
        
        // Select the "Vé xem phim" tab by default (index 2)
        TabLayout.Tab tab = tabLayout.getTabAt(2);
        if (tab != null) {
            tab.select();
        }
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(bookingList, this::onBookingItemClick);
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBookings.setAdapter(adapter);
    }




    private void loadBookings() {

        // TODO: MockAPI instead of dumb data
        createDummyBookings();
        
        // Update UI
        updateUI();
    }


    // TODO: MockAPI instead of dumb data
    private void createDummyBookings() {
        // Create dummy theatres
        Theatre galaxyLinhTrung = new Theatre();
        galaxyLinhTrung.setId("1");
        galaxyLinhTrung.setName("Galaxy Linh Trung");
        galaxyLinhTrung.setAddress("Tầng trệt | TTTM Co.opXtra Linh Trung, số 934 Quốc Lộ 1A, P. Linh Trung, Q. Thủ Đức, TP.HCM");
        galaxyLinhTrung.setLogoUrl("galaxy_logo");

        Theatre cinestarBinhDuong = new Theatre();
        cinestarBinhDuong.setId("2");
        cinestarBinhDuong.setName("CineStar Bình Dương");
        cinestarBinhDuong.setAddress("Tầng 5 | TTTM Aeon Mall Bình Dương, Thuận An, Bình Dương");
        cinestarBinhDuong.setLogoUrl("cinestar_logo");

        Theatre lotteCongHoa = new Theatre();
        lotteCongHoa.setId("3");
        lotteCongHoa.setName("Lotte Cộng Hòa");
        lotteCongHoa.setAddress("Tầng 4 | Pico Plaza, 20 Cộng Hòa, P.12, Q.Tân Bình, TP.HCM");
        lotteCongHoa.setLogoUrl("lotte_logo");

        // Create dummy movies
        Movie movie1 = new Movie();
        movie1.setId("1");
        movie1.setTitle("Nhà Giả Tiền");

        Movie movie2 = new Movie();
        movie2.setId("2");
        movie2.setTitle("Đèn Âm Hồn");

        Movie movie3 = new Movie();
        movie3.setId("3");
        movie3.setTitle("404 Chạy Ngay Đi");

        Movie movie4 = new Movie();
        movie4.setId("4");
        movie4.setTitle("Đẹp Trai Thấy Sai Sai");

        Movie movie5 = new Movie();
        movie5.setId("5");
        movie5.setTitle("Thám Tử Lừng Danh Conan: Tàu Ngầm Sắt Màu Đen");

        Movie movie6 = new Movie();
        movie6.setId("6");
        movie6.setTitle("Kẻ Trộm Mặt Trăng 4");

        // Create dummy showtimes
        Showtime showtime1 = new Showtime();
        showtime1.setId("1");
        showtime1.setMovieId(movie1.getId());
        showtime1.setMovie(movie1);
        showtime1.setTheatreId(galaxyLinhTrung.getId());
        showtime1.setTheatre(galaxyLinhTrung);
        showtime1.setStartTime(parseTime("13:50"));
        showtime1.setDate(parseDate("23/02/2025"));
        showtime1.setFormat("2D");
        showtime1.setLanguage("Phụ đề");

        Showtime showtime2 = new Showtime();
        showtime2.setId("2");
        showtime2.setMovieId(movie2.getId());
        showtime2.setMovie(movie2);
        showtime2.setTheatreId(cinestarBinhDuong.getId());
        showtime2.setTheatre(cinestarBinhDuong);
        showtime2.setStartTime(parseTime("16:30"));
        showtime2.setDate(parseDate("07/02/2025"));
        showtime2.setFormat("2D");
        showtime2.setLanguage("Phụ đề");

        Showtime showtime3 = new Showtime();
        showtime3.setId("3");
        showtime3.setMovieId(movie3.getId());
        showtime3.setMovie(movie3);
        showtime3.setTheatreId(cinestarBinhDuong.getId());
        showtime3.setTheatre(cinestarBinhDuong);
        showtime3.setStartTime(parseTime("17:15"));
        showtime3.setDate(parseDate("24/12/2024"));
        showtime3.setFormat("2D");
        showtime3.setLanguage("Phụ đề");

        Showtime showtime4 = new Showtime();
        showtime4.setId("4");
        showtime4.setMovieId(movie3.getId());
        showtime4.setMovie(movie3);
        showtime4.setTheatreId(cinestarBinhDuong.getId());
        showtime4.setTheatre(cinestarBinhDuong);
        showtime4.setStartTime(parseTime("14:10"));
        showtime4.setDate(parseDate("24/12/2024"));
        showtime4.setFormat("2D");
        showtime4.setLanguage("Phụ đề");

        Showtime showtime5 = new Showtime();
        showtime5.setId("5");
        showtime5.setMovieId(movie4.getId());
        showtime5.setMovie(movie4);
        showtime5.setTheatreId(lotteCongHoa.getId());
        showtime5.setTheatre(lotteCongHoa);
        showtime5.setStartTime(parseTime("18:40"));
        showtime5.setDate(parseDate("17/08/2024"));
        showtime5.setFormat("2D");
        showtime5.setLanguage("Phụ đề");

        Showtime showtime6 = new Showtime();
        showtime6.setId("6");
        showtime6.setMovieId(movie5.getId());
        showtime6.setMovie(movie5);
        showtime6.setTheatreId(cinestarBinhDuong.getId());
        showtime6.setTheatre(cinestarBinhDuong);
        showtime6.setStartTime(parseTime("17:50"));
        showtime6.setDate(parseDate("03/08/2024"));
        showtime6.setFormat("2D");
        showtime6.setLanguage("Phụ đề");

        Showtime showtime7 = new Showtime();
        showtime7.setId("7");
        showtime7.setMovieId(movie6.getId());
        showtime7.setMovie(movie6);
        showtime7.setTheatreId(cinestarBinhDuong.getId());
        showtime7.setTheatre(cinestarBinhDuong);
        showtime7.setStartTime(parseTime("19:20"));
        showtime7.setDate(parseDate("06/07/2024"));
        showtime7.setFormat("2D");
        showtime7.setLanguage("Phụ đề");

        // Create dummy bookings
        Booking booking1 = new Booking();
        booking1.setId("1");
        booking1.setShowtimeId(showtime1.getId());
        booking1.setShowtime(showtime1);
        booking1.setBookingCode("WNZS8ZS");
        booking1.setSeats(List.of("E07", "E08", "E09"));
        booking1.setTotalAmount(355000);
        booking1.setTransactionId("79781002660");
        booking1.setTransactionTime(parseDateTime("00:34", "23/02/2025"));
        booking1.setStatus("CONFIRMED");

        Booking booking2 = new Booking();
        booking2.setId("2");
        booking2.setShowtimeId(showtime2.getId());
        booking2.setShowtime(showtime2);
        booking2.setBookingCode("ABCD123");
        booking2.setSeats(List.of("F12", "F13"));
        booking2.setTotalAmount(240000);
        booking2.setTransactionId("79781002661");
        booking2.setTransactionTime(parseDateTime("14:22", "05/02/2025"));
        booking2.setStatus("CONFIRMED");

        Booking booking3 = new Booking();
        booking3.setId("3");
        booking3.setShowtimeId(showtime3.getId());
        booking3.setShowtime(showtime3);
        booking3.setBookingCode("EFGH456");
        booking3.setSeats(List.of("D08"));
        booking3.setTotalAmount(120000);
        booking3.setTransactionId("79781002662");
        booking3.setTransactionTime(parseDateTime("09:15", "22/12/2024"));
        booking3.setStatus("CONFIRMED");

        Booking booking4 = new Booking();
        booking4.setId("4");
        booking4.setShowtimeId(showtime4.getId());
        booking4.setShowtime(showtime4);
        booking4.setBookingCode("IJKL789");
        booking4.setSeats(List.of("G05", "G06"));
        booking4.setTotalAmount(240000);
        booking4.setTransactionId("79781002663");
        booking4.setTransactionTime(parseDateTime("10:45", "22/12/2024"));
        booking4.setStatus("CONFIRMED");

        Booking booking5 = new Booking();
        booking5.setId("5");
        booking5.setShowtimeId(showtime5.getId());
        booking5.setShowtime(showtime5);
        booking5.setBookingCode("MNOP012");
        booking5.setSeats(List.of("H10"));
        booking5.setTotalAmount(120000);
        booking5.setTransactionId("79781002664");
        booking5.setTransactionTime(parseDateTime("16:30", "15/08/2024"));
        booking5.setStatus("CONFIRMED");

        Booking booking6 = new Booking();
        booking6.setId("6");
        booking6.setShowtimeId(showtime6.getId());
        booking6.setShowtime(showtime6);
        booking6.setBookingCode("QRST345");
        booking6.setSeats(List.of("C07", "C08", "C09"));
        booking6.setTotalAmount(360000);
        booking6.setTransactionId("79781002665");
        booking6.setTransactionTime(parseDateTime("12:20", "01/08/2024"));
        booking6.setStatus("CONFIRMED");

        Booking booking7 = new Booking();
        booking7.setId("7");
        booking7.setShowtimeId(showtime7.getId());
        booking7.setShowtime(showtime7);
        booking7.setBookingCode("UVWX678");
        booking7.setSeats(List.of("B15", "B16"));
        booking7.setTotalAmount(240000);
        booking7.setTransactionId("79781002666");
        booking7.setTransactionTime(parseDateTime("18:05", "05/07/2024"));
        booking7.setStatus("CONFIRMED");

        // Add bookings to list
        bookingList.add(booking1);
        bookingList.add(booking2);
        bookingList.add(booking3);
        bookingList.add(booking4);
        bookingList.add(booking5);
        bookingList.add(booking6);
        bookingList.add(booking7);
    }

    private void updateUI() {
        if (bookingList.isEmpty()) {
            recyclerViewBookings.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewBookings.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private void filterBookingsByTab(int tabPosition) {
        // TODO: Mock API
        // For this example, we'll just show all bookings for the "Vé xem phim" tab
        // and hide them for other tabs
        
        // Check if bookingList is null to prevent NullPointerException
        if (bookingList == null) {
            recyclerViewBookings.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            return;
        }
        
        if (tabPosition == 2) { // "Vé xem phim" tab
            if (!bookingList.isEmpty()) {
                recyclerViewBookings.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
            } else {
                recyclerViewBookings.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
        } else {
            // For other tabs, show empty state
            recyclerViewBookings.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnClose.setOnClickListener(v -> finish());

        btnSupport.setOnClickListener(v -> {
            // Handle support button click
            // For example, show a support dialog or navigate to support screen
        });

        fabFilter.setOnClickListener(v -> {
            // Handle filter button click
            // For example, show a filter dialog
        });
    }

    private void onBookingItemClick(Booking booking) {
        // Navigate to booking details screen
        Intent intent = new Intent(this, BookingHistoryInfoActivity.class);
        intent.putExtra("booking", booking);
        startActivity(intent);
    }

    // Helper methods for parsing dates and times
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private Date parseTime(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.parse(timeStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private Date parseDateTime(String timeStr, String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(timeStr + " " + dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}