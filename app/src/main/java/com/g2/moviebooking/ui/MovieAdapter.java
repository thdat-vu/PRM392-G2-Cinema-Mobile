package com.g2.moviebooking.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Movie;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Glide.with(holder.itemView.getContext())
                .load(movie.getBanner())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageViewBanner);
        holder.titleTextView.setText(movie.getTitle());
        holder.ratingTextView.setText(String.format("%.1f", movie.getRating()));
        holder.genresTextView.setText(String.join(", ", movie.getGenres() != null ? movie.getGenres() : new String[]{}));
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBanner;
        TextView titleTextView, ratingTextView, genresTextView;

        MovieViewHolder(View itemView) {
            super(itemView);
            imageViewBanner = itemView.findViewById(R.id.image_view_banner);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            ratingTextView = itemView.findViewById(R.id.text_view_rating);
            genresTextView = itemView.findViewById(R.id.text_view_genres);
        }
    }
}