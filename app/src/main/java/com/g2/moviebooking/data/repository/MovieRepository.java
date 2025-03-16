package com.g2.moviebooking.data.repository;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.utils.FirebaseClient;
import java.util.List;

public class MovieRepository {
    private final FirebaseFirestore db;
    private static final int PAGE_SIZE = 10;

    public MovieRepository(Context context) {
        db = FirebaseClient.getFirestore();
    }

    // Gán ID cho Movie
    private void setMovieId(Movie movie, String id) {
        if (movie != null) {
            movie.setId(id);
        }
    }

    // Lấy danh sách phim với phân trang
    public void getMovies(int pageNum, MovieCallback<List<Movie>> callback) {
        Query query = db.collection("movies")
                .orderBy("title")
                .limit(PAGE_SIZE);

        if (pageNum > 1) {
            query = query.startAfter((pageNum - 1) * PAGE_SIZE);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Movie> movies = querySnapshot.toObjects(Movie.class);
                    for (int i = 0; i < movies.size(); i++) {
                        setMovieId(movies.get(i), querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(movies);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Lấy chi tiết phim
    public void getMovieDetail(String movieId, MovieCallback<Movie> callback) {
        db.collection("movies").document(movieId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Movie movie = documentSnapshot.toObject(Movie.class);
                    setMovieId(movie, documentSnapshot.getId());
                    callback.onSuccess(movie);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Tìm phim theo thể loại
    public void getMoviesByGenre(String genre, MovieCallback<List<Movie>> callback) {
        db.collection("movies")
                .whereArrayContains("genres", genre)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Movie> movies = querySnapshot.toObjects(Movie.class);
                    for (int i = 0; i < movies.size(); i++) {
                        setMovieId(movies.get(i), querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(movies);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Tìm kiếm phim theo tên
    public void searchMoviesByTitle(String keyword, MovieCallback<List<Movie>> callback) {
        db.collection("movies")
                .orderBy("title")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff") // unicode trick
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Movie> movies = querySnapshot.toObjects(Movie.class);
                    for (int i = 0; i < movies.size(); i++) {
                        setMovieId(movies.get(i), querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(movies);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    // Interface callback
    public interface MovieCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}