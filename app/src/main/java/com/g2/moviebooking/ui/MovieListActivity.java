package com.g2.moviebooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Movie;
import com.g2.moviebooking.data.repository.MovieRepository;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recycler_view_movies);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Thêm padding để item đầu tiên không bị cắt khi scroll
        int padding = getResources().getDimensionPixelSize(R.dimen.recycler_view_padding);
        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setClipToPadding(false);

        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // Thêm PagerSnapHelper để lướt từng item và căn giữa
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Thêm listener để theo dõi item focus và áp dụng hiệu ứng scale
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

        // Khởi tạo Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set active item là Movies
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);

        movieRepository = new MovieRepository(this);
        fetchMovies();
    }

    private void applyScaleEffect() {
        int centerOfScreen = recyclerView.getWidth() / 2;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            int childCenter = (child.getLeft() + child.getRight()) / 2;
            float distanceFromCenter = Math.abs(childCenter - centerOfScreen);

            // Tính toán scale dựa trên khoảng cách từ center
            float scale = Math.max(0.85f, 1f - 0.15f * (distanceFromCenter / centerOfScreen));

            // Áp dụng scale cho view
            child.setScaleX(scale);
            child.setScaleY(scale);

            // Điều chỉnh độ trong suốt (tùy chọn)
            float alpha = Math.max(0.7f, 1f - 0.3f * (distanceFromCenter / centerOfScreen));
            child.setAlpha(alpha);

            // Item ở center sẽ hiển thị phía trước
            if (scale > 0.95f) {
                child.setElevation(10f);
            } else {
                child.setElevation(5f);
            }
        }
    }

    private void fetchMovies() {
        movieRepository.getMovies(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieAdapter.updateMovies(response.body());

                    // Đảm bảo item đầu tiên được hiển thị đúng
                    recyclerView.post(() -> {
                        if (!response.body().isEmpty()) {
                            recyclerView.scrollToPosition(0);
                            // Cần áp dụng hiệu ứng scale sau khi scroll
                            applyScaleEffect();
                        }
                    });
                } else {
                    Toast.makeText(MovieListActivity.this, "Lỗi khi tải danh sách phim: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Toast.makeText(MovieListActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fix lỗi khi quay lại không hiển thị đúng
        if (movieAdapter.getItemCount() > 0) {
            recyclerView.post(this::applyScaleEffect);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_movies) {
            // Đã ở màn hình phim, không cần thay đổi
            return true;
        } else if (itemId == R.id.nav_theatres){
            // Chuyển đến màn hình rạp
            navigateToTheatres();
            return true;
        } else if (itemId == R.id.nav_tickets) {
            // Chuyển đến màn hình vé của tôi
            navigateToMyTickets();
            return true;
        } else if (itemId == R.id.nav_profile) {
            // Chuyển đến màn hình tài khoản
            navigateToProfile();
            return true;
        }
        return false;
    }

    // Các phương thức chuyển màn hình

    private void navigateToTheatres(){
        //Intent intent = new Intent(this, TheaterListActivity.class);
        //startActivity(intent);
        Toast.makeText(this, "Chuyển đến màn hình chọn Rạp", Toast.LENGTH_SHORT).show();
    }

    private void navigateToMyTickets() {
        // Intent intent = new Intent(this, MyTicketsActivity.class);
        // startActivity(intent);
        Toast.makeText(this, "Chuyển đến màn hình Vé của tôi", Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile() {
        // Intent intent = new Intent(this, ProfileActivity.class);
        // startActivity(intent);
        Toast.makeText(this, "Chuyển đến màn hình Tài khoản", Toast.LENGTH_SHORT).show();
    }
}