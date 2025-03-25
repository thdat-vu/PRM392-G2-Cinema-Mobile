package com.g2.moviebooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.g2.moviebooking.R;
import com.g2.moviebooking.adapter.BannerAdapter;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.ui.bookings.BookingHistoryListActivity;
import com.g2.moviebooking.ui.tickets.TicketsActivity;
import com.g2.moviebooking.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {
    private static final int PAGE_SIZE = Constants.PAGE_SIZE;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private LinearLayoutManager layoutManager;
    private MovieRepository movieRepository;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreMovies = true;
    private List<Movie> allMovies = new ArrayList<>();
    private SearchView searchView;
    private ViewPager2 bannerSlider;
    private RecyclerView recyclerMovies;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<String> bannerUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        bannerSlider = findViewById(R.id.banner_slider);

        // Danh sách URL ảnh banner (có thể lấy từ API)
        bannerUrls = Arrays.asList(
                "https://media1.thehungryjpeg.com/thumbs2/ori_3746269_ix81djx2q4racxqk2170f29bw9lb2yu23aq8o7je_online-cinema-banner-vector-realistic-computer-monitor-movie-brochure-design-template-banner-for-movie-premiere-show-blue-curtain-theater-marketing-luxury-poster-illustration.jpg",
                "https://media1.thehungryjpeg.com/thumbs2/ori_3746269_ix81djx2q4racxqk2170f29bw9lb2yu23aq8o7je_online-cinema-banner-vector-realistic-computer-monitor-movie-brochure-design-template-banner-for-movie-premiere-show-blue-curtain-theater-marketing-luxury-poster-illustration.jpg",
                "https://media1.thehungryjpeg.com/thumbs2/ori_3746269_ix81djx2q4racxqk2170f29bw9lb2yu23aq8o7je_online-cinema-banner-vector-realistic-computer-monitor-movie-brochure-design-template-banner-for-movie-premiere-show-blue-curtain-theater-marketing-luxury-poster-illustration.jpg"
        );
        BannerAdapter bannerAdapter = new BannerAdapter(this, bannerUrls);
        bannerSlider.setAdapter(bannerAdapter);

        // Tự động chuyển đổi banner mỗi 3 giây
        startAutoSlide();
        movieRepository = new MovieRepository(this);
        setupRecyclerView();
        setupSearchView();
        setupBottomNavigation();
        fetchMovies(currentPage);
    }

    private void startAutoSlide() {
        final Runnable autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = bannerSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerUrls.size(); // Quay vòng
                bannerSlider.setCurrentItem(nextItem, true); // Chuyển trang
                handler.postDelayed(this, 3000); // Lặp lại sau 3 giây
            }
        };

        handler.postDelayed(autoSlideRunnable, 3000); // Bắt đầu auto-slide sau 3 giây
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


    private void updateUI() {
        if (movieAdapter.getItemCount() == 0) {
            findViewById(R.id.recycler_view_movies).setVisibility(View.GONE);
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.recycler_view_movies).setVisibility(View.VISIBLE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

    private void setupSearchView() {
        searchView = findViewById(R.id.search_view_movies);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMoviesByTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search text is cleared, show all movies
                    movieAdapter.updateMovies(allMovies);
                } else {
                    // Search as user types
                    searchMoviesByTitle(newText);
                }
                return true;
            }
        });
    }

    private void searchMoviesByTitle(String query) {
        if (query.isEmpty()) {
            movieAdapter.updateMovies(allMovies);
            return;
        }

        List<Movie> filteredMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredMovies.add(movie);
            }
        }

        if (filteredMovies.isEmpty()) {
            showToast("Không tìm thấy phim nào phù hợp");
        }

        movieAdapter.updateMovies(filteredMovies);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_movies) {
                return true;
            } else if (itemId == R.id.nav_theatres) {
                showToast("Chức năng rạp chưa được triển khai");
                return true;
            } else if (itemId == R.id.nav_tickets) {
                startActivity(new Intent(this, BookingHistoryListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_movies); // Default selection
    }

    private void fetchMovies(int pageNum) {
        if (!hasMoreMovies || isLoading) return;
        isLoading = true;
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        movieRepository.getMovies(pageNum, new MovieRepository.MovieCallback<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                isLoading = false;
                if (movies.size() < PAGE_SIZE) {
                    hasMoreMovies = false;
                }
                allMovies.addAll(movies);
                movieAdapter.addMovies(movies);
                updateUI();
            }

            @Override
            public void onFailure(String error) {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                isLoading = false;
                showToast("Lỗi khi tải danh sách phim: " + error);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null); // Ngừng auto-slide khi Activity dừng
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoSlide(); // Tiếp tục auto-slide khi Activity quay lại
    }
}