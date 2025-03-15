package com.g2.moviebooking.ui;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Entity.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        // Tải banner bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(movie.getBanner())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageViewBanner);

        holder.titleTextView.setText(movie.getTitle());
        holder.ratingTextView.setText(String.format("%.1f", movie.getRating()));
        holder.genresTextView.setText(String.join(", ", movie.getGenres() != null ? movie.getGenres() : new String[]{}));

        // Thêm sự kiện double-click
        GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        Intent intent = new Intent(holder.itemView.getContext(), MovieDetailActivity.class);
                        intent.putExtra("MOVIE_ID", movie.getId()); // Giả định Movie có getId()
                        holder.itemView.getContext().startActivity(intent);
                        return true;
                    }
                });

        holder.itemView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Thêm danh sách phim mới
    public void addMovies(List<Movie> newMovies) {
        int startPosition = movies.size();
        movies.addAll(newMovies);
        notifyItemRangeInserted(startPosition, newMovies.size());
    }

    // Cập nhật toàn bộ danh sách phim
    public void updateMovies(List<Movie> newMovies) {
        movies.clear();
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBanner;
        TextView titleTextView;
        TextView ratingTextView;
        TextView genresTextView;

        MovieViewHolder(View itemView) {
            super(itemView);
            imageViewBanner = itemView.findViewById(R.id.image_view_banner);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            ratingTextView = itemView.findViewById(R.id.text_view_rating);
            genresTextView = itemView.findViewById(R.id.text_view_genres);
        }
    }
}