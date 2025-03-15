package com.g2.moviebooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.ui.tickets.TicketsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {
    private static final int PAGE_SIZE = 10;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private LinearLayoutManager layoutManager;
    private MovieRepository movieRepository;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreMovies = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        movieRepository = new MovieRepository(this);
        setupRecyclerView();
        setupBottomNavigation();
        fetchMovies(currentPage);
    }

    private void setupRecyclerView() {
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
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                    currentPage++;
                    fetchMovies(currentPage);
                }
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_movies) {
                return true;
            } else if (itemId == R.id.nav_theatres) {
                // TODO: Implement TheatresActivity
                showToast("Chức năng rạp chưa được triển khai");
                return true;
            } else if (itemId == R.id.nav_tickets) {
                startActivity(new Intent(this, TicketsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Implement ProfileActivity
                showToast("Chức năng tài khoản chưa được triển khai");
                return true;
            }

            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_movies); // Default selection
    }

    private void fetchMovies(int pageNum) {
        if (!hasMoreMovies || isLoading) return;
        isLoading = true;
        movieRepository.getMovies(pageNum, new MovieRepository.MovieCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                isLoading = false;
                if (movies.size() < PAGE_SIZE) {
                    hasMoreMovies = false;
                }
                movieAdapter.addMovies(movies);
            }

            @Override
            public void onFailure(String error) {
                isLoading = false;
                showToast("Lỗi khi tải danh sách phim: " + error);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}