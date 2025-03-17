package com.g2.moviebooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.ui.ShowtimeSelectionActivity;
import com.g2.moviebooking.utils.Constants;

import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {
    private ImageView ivBanner;
    private TextView tvTitle, tvDescription, tvGenres, tvReleaseDate, tvDuration, tvDirector, tvActors, tvRating;
    private Button btnSelectShowtime;
    private MovieRepository movieRepository;
    private Movie movie;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieRepository = new MovieRepository(this);
        setupViews();

        String movieId = getIntent().getStringExtra("MOVIE_ID");
        if (movieId != null) {
            fetchMovieDetail(movieId);
        } else {
            showToast("Không tìm thấy ID phim");
            finish();
        }
    }

    private void setupViews() {
        ivBanner = findViewById(R.id.iv_movie_banner);
        tvTitle = findViewById(R.id.tv_movie_title);
        tvDescription = findViewById(R.id.tv_movie_description);
        tvGenres = findViewById(R.id.tv_movie_genres);
        tvReleaseDate = findViewById(R.id.tv_movie_release_date);
        tvDuration = findViewById(R.id.tv_movie_duration);
        tvDirector = findViewById(R.id.tv_movie_director);
        tvActors = findViewById(R.id.tv_movie_actors);
        tvRating = findViewById(R.id.tv_movie_rating);
        btnSelectShowtime = findViewById(R.id.btn_select_showtime);
        btnBack = findViewById(R.id.buttonBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        // Handle button click to go to ShowtimeSelectionActivity
        btnSelectShowtime.setOnClickListener(v -> {
            if (movie != null) {
                Intent intent = new Intent(MovieDetailActivity.this, ShowtimeSelectionActivity.class);
                intent.putExtra("MOVIE_ID", movie.getId());
                intent.putExtra("MOVIE_TITLE", movie.getTitle());
                intent.putExtra(Constants.EXTRA_MOVIE, movie);
                startActivity(intent);
            } else {
                showToast("Vui lòng chờ thông tin phim được tải");
            }
        });
    }

    private void fetchMovieDetail(String movieId) {
        movieRepository.getMovieDetail(movieId, new MovieRepository.MovieCallback<Movie>() {
            @Override
            public void onSuccess(Movie movieResult) {
                movie = movieResult; // Store the movie for later use
                displayMovieDetail(movie);
            }

            @Override
            public void onFailure(String error) {
                showToast("Lỗi khi tải thông tin phim: " + error);
            }
        });
    }

    private void displayMovieDetail(Movie movie) {
        Log.d("MovieDetailActivity", "Banner URL: " + movie.getBannerUrl());
        Glide.with(this)
                .load(movie.getBannerUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivBanner);

        setTextOrDefault(tvTitle, movie.getTitle(), "Không có tiêu đề");
        setTextOrDefault(tvDescription, movie.getDescription(), "Không có mô tả");
        setTextOrDefault(tvGenres, joinList(movie.getGenres()), "Không có thể loại");
        setTextOrDefault(tvReleaseDate, movie.getReleaseDate(), "Không có ngày phát hành");
        setTextOrDefault(tvDuration, movie.getDuration() + " phút", "Không có thời lượng");
        setTextOrDefault(tvDirector, movie.getDirector(), "Không có đạo diễn");
        setTextOrDefault(tvActors, joinList(movie.getActors()), "Không có diễn viên");
        setTextOrDefault(tvRating, String.format("%.1f", movie.getRating()), "Không có đánh giá");
    }

    private void setTextOrDefault(TextView textView, String text, String defaultText) {
        textView.setText(text != null && !text.isEmpty() ? text : defaultText);
    }

    private String joinList(List<String> list) {
        return list != null && !list.isEmpty() ? String.join(", ", list) : "";
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}