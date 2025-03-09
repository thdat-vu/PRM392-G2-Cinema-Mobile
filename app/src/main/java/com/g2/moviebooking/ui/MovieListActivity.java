package com.g2.moviebooking.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Entity.Movie;
import com.g2.moviebooking.data.remote.model.Response.MovieResponse;
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

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

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

        setupScrollListener();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);

        movieRepository = new MovieRepository(this);
        fetchMovies(currentPage);
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                applyScaleEffect();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && currentPage < totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                        currentPage++;
                        fetchMovies(currentPage);
                    }
                }
            }
        });
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

            child.setElevation(scale > 0.95f ? 10f : 5f);
        }
    }

    private void fetchMovies(int pageNum) {
        isLoading = true;

        movieRepository.getMovies(pageNum, PAGE_SIZE).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Movie> newMovies = response.body().getData().getPageData();
                    totalPages = response.body().getData().getPageInfo().getTotalPages();

                    movieAdapter.addMovies(newMovies);
                    recyclerView.post(() -> applyScaleEffect());
                } else {
                    Toast.makeText(MovieListActivity.this, "Lỗi khi tải danh sách phim: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(MovieListActivity.this, "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_movies) return true;
        else if (itemId == R.id.nav_theatres) { navigateToTheatres(); return true; }
        else if (itemId == R.id.nav_tickets) { navigateToMyTickets(); return true; }
        else if (itemId == R.id.nav_profile) { navigateToProfile(); return true; }
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