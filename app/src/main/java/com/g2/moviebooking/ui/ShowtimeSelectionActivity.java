package com.g2.moviebooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.moviebooking.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Showtime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShowtimeSelectionActivity extends AppCompatActivity {

    private static final String TAG = "ShowtimeSelectionActivity";
    private TextView tvMovieTitle;
    private Spinner spinnerDates;
    private ListView lvShowtimes;
    private String movieId;
    private String movieTitle;
    private List<Date> availableDates;
    private List<Showtime> showtimes;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtime_selection);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get movieId and movieTitle from intent
        movieId = getIntent().getStringExtra("MOVIE_ID");
        movieTitle = getIntent().getStringExtra("MOVIE_TITLE");
        Log.d(TAG, "Movie ID: " + movieId + ", Movie Title: " + movieTitle);

        if (movieId == null || movieTitle == null) {
            Toast.makeText(this, "Không tìm thấy thông tin phim", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        tvMovieTitle = findViewById(R.id.tv_movie_title);
        spinnerDates = findViewById(R.id.spinner_dates);
        lvShowtimes = findViewById(R.id.lv_showtimes);

        // Set movie title
        tvMovieTitle.setText(movieTitle);

        // Initialize data
        showtimes = new ArrayList<>();
        availableDates = generateDateList();

        // Setup date spinner
        setupDateSpinner();

        // Setup showtimes list
        setupShowtimesList();
    }

    private List<Date> generateDateList() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.MARCH, 15); // Set to March 15, 2025

        for (int i = 0; i < 7; i++) { // 7 days from March 15
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

        // Handle date selection
        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Date selectedDate = availableDates.get(position);
                Log.d(TAG, "Selected Date: " + dateFormat.format(selectedDate));
                fetchShowtimesForDate(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void fetchShowtimesForDate(Date selectedDate) {
        // Normalize the selected date to the start of the day in UTC
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        selectedCal.set(Calendar.HOUR_OF_DAY, 0);
        selectedCal.set(Calendar.MINUTE, 0);
        selectedCal.set(Calendar.SECOND, 0);
        selectedCal.set(Calendar.MILLISECOND, 0);

        // Convert to UTC
        selectedCal.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startOfDay = selectedCal.getTime();
        Log.d(TAG, "Querying for movieId: " + movieId + ", date: " + startOfDay + " (UTC timestamp: " + startOfDay.getTime() + ")");

        // Query Firestore for showtimes on the selected date for the selected movie
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("date", startOfDay)
                .orderBy("startTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    showtimes.clear();
                    Log.d(TAG, "Query returned " + querySnapshot.size() + " documents");
                    for (var doc : querySnapshot) {
                        Showtime showtime = doc.toObject(Showtime.class);
                        showtime.setId(doc.getId());
                        showtimes.add(showtime);
                        Log.d(TAG, "Showtime: " + timeFormat.format(showtime.getStartTime()) + " at " + showtime.getTheatre().getName());
                    }
                    updateShowtimesList();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching showtimes: " + e.getMessage());
                    Toast.makeText(ShowtimeSelectionActivity.this,
                            "Lỗi khi tải suất chiếu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showtimes.clear();
                    updateShowtimesList();
                });
    }

    private void setupShowtimesList() {
        ArrayAdapter<Showtime> adapter = new ArrayAdapter<Showtime>(this,
                android.R.layout.simple_list_item_1, showtimes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                Showtime showtime = showtimes.get(position);
                String displayText = String.format("%s - %s (%s) - %s",
                        timeFormat.format(showtime.getStartTime()),
                        showtime.getTheatre().getName(),
                        showtime.getFormat(),
                        showtime.getLanguage());
                textView.setText(displayText);
                textView.setOnClickListener(v -> {
                    Intent intent = new Intent(ShowtimeSelectionActivity.this, SeatActivity.class);
                    intent.putExtra(Constants.EXTRA_SHOWTIME, showtime);
                    startActivity(intent);
                });
                return textView;
            }
        };
        lvShowtimes.setAdapter(adapter);
    }

    private void updateShowtimesList() {
        ((ArrayAdapter) lvShowtimes.getAdapter()).notifyDataSetChanged();
    }
}