package com.g2.moviebooking.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.adapter.DateAdapter;
import com.g2.moviebooking.adapter.TheatreAdapter;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.ui.bookings.SeatActivity;
import com.g2.moviebooking.utils.Constants;
import com.g2.moviebooking.utils.LocationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;
import com.g2.moviebooking.data.repository.ShowtimeRepository;
import com.g2.moviebooking.data.repository.TheatreRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class ShowtimeSelectionActivity extends AppCompatActivity {

    private static final String TAG = "ShowtimeSelectionActivity";
    private ImageView btnBack;
    private TextView tvMovieTitle;
    private RecyclerView recyclerDates;
    private String movieId;
    private String movieTitle;
    private Movie movie;
    private List<Date> availableDates;
    private List<Showtime> showtimes;
    private ShowtimeRepository showtimeRepository;
    private TheatreRepository theatreRepository;
    private Map<String, Theatre> theatreMap; // Lưu trữ thông tin rạp theo theatreId
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private RecyclerView rvTheatres;
    private TheatreAdapter theatreAdapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude;
    private double currentLongitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtime_selection);

        // Get movieId and movieTitle from intent
        movieId = getIntent().getStringExtra("MOVIE_ID");
        movieTitle = getIntent().getStringExtra("MOVIE_TITLE");
        movie = (Movie) getIntent().getSerializableExtra(Constants.EXTRA_MOVIE);

        Log.d(TAG, "Movie ID: " + movieId + ", Movie Title: " + movieTitle);

        if (movieId == null || movieTitle == null) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        tvMovieTitle = findViewById(R.id.tv_movie_title);
        recyclerDates = findViewById(R.id.recycler_dates);
        rvTheatres = findViewById(R.id.rvTheatres);

        // Set movie title
        tvMovieTitle.setText(movieTitle);

        // Initialize repositories
        showtimeRepository = new ShowtimeRepository();
        theatreRepository = new TheatreRepository();
        theatreMap = new HashMap<>();

        // Initialize data
        showtimes = new ArrayList<>();
        availableDates = generateDateList();

        // Load all theatres upfront
        loadTheatres();

        // Get current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissionAndFetch();



        // Setup UI
        setupDateRecyclerView();

        btnBack.setOnClickListener(v -> onBackPressed());
    }
    private void checkLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not already granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions are granted; now fetch the current location using your utility class
            LocationUtils.getCurrentLocation(this, fusedLocationProviderClient, new LocationUtils.LocationCallback() {
                @Override
                public void onLocationResult(double latitude, double longitude) {
                    currentLatitude = latitude;
                    currentLongitude = longitude;
                    Log.d(TAG, "Current location: " + latitude + ", " + longitude);
                    updateShowtimesList();
                }

                @Override
                public void onLocationFailure(Exception e) {
                    Log.e(TAG, "Failed to get current location", e);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted; try to fetch the location again.
                checkLocationPermissionAndFetch();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Date> generateDateList() {
        List<Date> dates = new ArrayList<>();
        // Get current date in GMT+07:00 timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        // Set to start of today
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 14; i++) { // Generate 14 days
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
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
                Toast.makeText(ShowtimeSelectionActivity.this, "Lỗi khi tải danh sách rạp: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchShowtimesForDate(Date selectedDate) {
        Calendar selectedCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        selectedCal.setTime(selectedDate);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        Date startOfDay = selectedCal.getTime();

        Log.d(TAG, "Querying showtimes for movieId: " + movieId + ", date: " + startOfDay);

        showtimeRepository.getShowtimesByMovieId(movieId, new ShowtimeRepository.ShowtimeCallback<>() {
            @Override
            public void onSuccess(List<Showtime> result) {
                Log.d(TAG, "Showtimes fetched: " + result.size());
                for (Showtime showtime : result) {
                    Log.d(TAG, "Showtime ID: " + showtime.getId() + ", Date: " + dateFormat.format(showtime.getDate()));
                    // Gán thông tin theatre từ theatreMap
                    Theatre theatre = theatreMap.get(showtime.getTheatreId());
                    if (theatre != null) {
                        showtime.setTheatre(theatre);
                    } else {
                        Log.w(TAG, "Theatre not found for theatreId: " + showtime.getTheatreId());
                    }
                }
                showtimes.clear();
                for (Showtime showtime : result) {
                    if (isSameDay(showtime.getDate(), selectedDate)) {
                        Log.d(TAG, "Matched showtime: " + showtime.getId() + ", Start Time: " + timeFormat.format(showtime.getStartTime()));
                        showtimes.add(showtime);
                    }
                }
                Collections.sort(showtimes, Comparator.comparing(Showtime::getStartTime));
                updateShowtimesList();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to fetch showtimes: " + error);
                Toast.makeText(ShowtimeSelectionActivity.this, "Lỗi khi tải suất chiếu: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        cal2.setTime(date2);
        Log.d(TAG, "Comparing: " + dateFormat.format(date1) + " vs " + dateFormat.format(date2));
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void updateShowtimesList() {
        Log.d(TAG, "Updating showtimes list with " + showtimes.size() + " items");
        // Get theature list that contain suitable showtime
        // 1) Collect theatre IDs into a Set
        Set<String> theatreIdSet = showtimes.stream()
                .map(Showtime::getTheatreId)
                .collect(Collectors.toSet());

        // 2) Filter the map’s values based on that Set
        List<Theatre> theatreList = theatreMap.values().stream()
                .filter(t -> theatreIdSet.contains(t.getId()))
                .collect(Collectors.toList());

        // 3) Pass to adapter
        theatreAdapter = new TheatreAdapter(theatreList, showtimes, this, currentLatitude, currentLongitude);
        rvTheatres.setAdapter(theatreAdapter);

        // Typically a vertical list of theaters
        rvTheatres.setLayoutManager(new LinearLayoutManager(this));
    }
    private void setupDateRecyclerView() {
        // Create an adapter, pass in availableDates
        DateAdapter dateAdapter = new DateAdapter(availableDates, (date, position) -> {
            // Same logic as spinner onItemSelected
            Log.d(TAG, "Clicked date: " + dateFormat.format(date));
            fetchShowtimesForDate(date);
        });

        recyclerDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerDates.setAdapter(dateAdapter);

        // Optionally, load showtimes for the first date immediately:
        if (!availableDates.isEmpty()) {
            fetchShowtimesForDate(availableDates.get(0));
        }
    }
}