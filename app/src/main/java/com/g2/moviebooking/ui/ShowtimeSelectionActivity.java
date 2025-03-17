package com.g2.moviebooking.ui;

import android.content.Intent;
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


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.ui.bookings.SeatActivity;
import com.g2.moviebooking.utils.Constants;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ShowtimeSelectionActivity extends AppCompatActivity {

    private static final String TAG = "ShowtimeSelectionActivity";
    private ImageView btnBack;
    private TextView tvMovieTitle;
    private Spinner spinnerDates;
    private ListView lvShowtimes;
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
        spinnerDates = findViewById(R.id.spinner_dates);
        lvShowtimes = findViewById(R.id.lv_showtimes);

        // Set movie title
        tvMovieTitle.setText(movieTitle);

        // Initialize repositories
        showtimeRepository = new ShowtimeRepository();
        theatreRepository = new TheatreRepository();
        theatreMap = new HashMap<>();

        // Initialize data
        showtimes = new ArrayList<>();
        availableDates = generateDateList();

        // Setup UI
        setupDateSpinner();
        setupShowtimesList();

        // Load all theatres upfront
        loadTheatres();


        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private List<Date> generateDateList() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        calendar.set(2025, Calendar.MARCH, 15); // Set to March 15, 2025
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 7; i++) { // 7 days
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void setupDateSpinner() {
        List<String> dateStrings = new ArrayList<>();
        for (Date date : availableDates) {
            dateStrings.add(dateFormat.format(date));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dateStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDates.setAdapter(adapter);

        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Date selectedDate = availableDates.get(position);
                Log.d(TAG, "Selected Date: " + dateFormat.format(selectedDate));
                fetchShowtimesForDate(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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

        showtimeRepository.getShowtimesByMovieId(movieId, new ShowtimeRepository.ShowtimeCallback<List<Showtime>>() {
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
                Collections.sort(showtimes, (s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));
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

    private void setupShowtimesList() {
        ArrayAdapter<Showtime> adapter = new ArrayAdapter<Showtime>(this,
                android.R.layout.simple_list_item_1, showtimes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                Showtime showtime = showtimes.get(position);
                String theatreName = (showtime.getTheatre() != null) ? showtime.getTheatre().getName() : "Rạp không xác định";
                String displayText = String.format("%s - %s (%s) - %s",
                        timeFormat.format(showtime.getStartTime()),
                        theatreName,
                        showtime.getFormat(),
                        showtime.getLanguage());
                textView.setText(displayText);
                textView.setOnClickListener(v -> {
                    Intent intent = new Intent(ShowtimeSelectionActivity.this, SeatActivity.class);
                    showtime.setMovie(movie);
                    intent.putExtra(Constants.EXTRA_SHOWTIME, showtime);
                    startActivity(intent);
                });
                return textView;
            }
        };
        lvShowtimes.setAdapter(adapter);
    }

    private void updateShowtimesList() {
        Log.d(TAG, "Updating showtimes list with " + showtimes.size() + " items");
        ArrayAdapter adapter = (ArrayAdapter) lvShowtimes.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Adapter is null, cannot update showtimes list");
        }
    }
}