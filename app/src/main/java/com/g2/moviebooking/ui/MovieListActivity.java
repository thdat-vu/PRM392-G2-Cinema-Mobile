package com.g2.moviebooking.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Entity.Movie;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieRepository movieRepository;
    private LinearLayoutManager layoutManager;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;

    private static final int PAGE_NUM = 1;
    private static final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        recyclerView = findViewById(R.id.recycler_view_movies);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        int padding = getResources().getDimensionPixelSize(R.dimen.recycler_view_padding);
        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setClipToPadding(false);

        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                applyScaleEffect();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    applyScaleEffect();
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);

        progressBar = findViewById(R.id.progress_bar);

        movieRepository = new MovieRepository(this);
        fetchMovies();
    }

    private void applyScaleEffect() {
        int centerOfScreen = recyclerView.getWidth() / 2;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            int childCenter = (child.getLeft() + child.getRight()) / 2;
            float distanceFromCenter = Math.abs(childCenter - centerOfScreen);

            float scale = Math.max(0.85f, 1f - 0.15f * (distanceFromCenter / centerOfScreen));
            child.setScaleX(scale);
            child.setScaleY(scale);

            float alpha = Math.max(0.7f, 1f - 0.3f * (distanceFromCenter / centerOfScreen));
            child.setAlpha(alpha);

            if (scale > 0.95f) {
                child.setElevation(10f);
            } else {
                child.setElevation(5f);
            }
        }
    }

    private void fetchMovies() {
        progressBar.setVisibility(View.VISIBLE);
        movieRepository.getMovies(PAGE_NUM, PAGE_SIZE, new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();
                    movieAdapter.updateMovies(movies);
                    recyclerView.post(() -> {
                        if (!movies.isEmpty()) {
                            recyclerView.scrollToPosition(0);
                            applyScaleEffect();
                        }
                    });
                } else {
                    Toast.makeText(MovieListActivity.this, "Lỗi khi tải danh sách phim: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MovieListActivity.this, "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieAdapter.getItemCount() > 0) {
            recyclerView.post(this::applyScaleEffect);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_movies) {
            return true;
        } else if (itemId == R.id.nav_theatres) {
            navigateToTheatres();
            return true;
        } else if (itemId == R.id.nav_tickets) {
            navigateToMyTickets();
            return true;
        } else if (itemId == R.id.nav_profile) {
            navigateToProfile();
            return true;
        }
        return false;
    }

    private void navigateToTheatres() {
        Toast.makeText(this, "Chuyển đến màn hình chọn Rạp", Toast.LENGTH_SHORT).show();
    }

    private void navigateToMyTickets() {
        Toast.makeText(this, "Chuyển đến màn hình Vé của tôi", Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile() {
        Toast.makeText(this, "Chuyển đến màn hình Tài khoản", Toast.LENGTH_SHORT).show();
    }
}