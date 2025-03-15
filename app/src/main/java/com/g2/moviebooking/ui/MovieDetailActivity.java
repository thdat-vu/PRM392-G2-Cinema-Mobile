package com.g2.moviebooking.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Entity.Movie;
import com.g2.moviebooking.data.remote.model.Response.MovieDetailResponse;
import com.g2.moviebooking.data.repository.MovieRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    // UI components
    private ImageView ivBanner;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvGenres;
    private TextView tvReleaseDate;
    private TextView tvDuration;
    private TextView tvDirector;
    private TextView tvActors;
    private TextView tvRating;

    // Data components
    private MovieRepository movieRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Khởi tạo repository
        movieRepository = new MovieRepository(this);

        // Khởi tạo UI
        setupViews();

        // Lấy movieId từ Intent
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
    }

    private void fetchMovieDetail(String movieId) {
        movieRepository.getMovieDetail(movieId).enqueue(new Callback<MovieDetailResponse>() {
            @Override
            public void onResponse(Call<MovieDetailResponse> call, Response<MovieDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Movie movie = response.body().getData(); // Lấy trực tiếp data
                    displayMovieDetail(movie);
                } else {
                    showToast("Lỗi khi tải thông tin phim: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieDetailResponse> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    private void displayMovieDetail(Movie movie) {
        Glide.with(this)
                .load(movie.getBanner())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivBanner);

        tvTitle.setText(movie.getTitle());
        tvDescription.setText(movie.getDescription());
        tvGenres.setText(String.join(", ", movie.getGenres() != null ? movie.getGenres() : new String[]{}));
        tvReleaseDate.setText(movie.getReleaseDate());
        tvDuration.setText(movie.getDuration() + " phút");
        tvDirector.setText(movie.getDirector());
        tvActors.setText(String.join(", ", movie.getActors() != null ? movie.getActors() : new String[]{}));
        tvRating.setText(String.format("%.1f", movie.getRating()));
    }

    // Hiển thị thông báo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}