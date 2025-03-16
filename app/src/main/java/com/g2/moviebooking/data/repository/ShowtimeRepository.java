package com.g2.moviebooking.data.repository;

import com.g2.moviebooking.data.model.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ShowtimeRepository {
    private final FirebaseFirestore db;

    public ShowtimeRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy tất cả showtimes theo movie
    public void getShowtimesByMovieId(String movieId, ShowtimeCallback<List<Showtime>> callback) {
        db.collection("showtimes_refactored")
                .whereEqualTo("movieId", movieId)
                .orderBy("startTime")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Showtime> showtimes = querySnapshot.toObjects(Showtime.class);
                    for (int i = 0; i < showtimes.size(); i++) {
                        showtimes.get(i).setId(querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(showtimes);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Lấy chi tiết showtime
    public void getShowtimeDetail(String showtimeId, ShowtimeCallback<Showtime> callback) {
        db.collection("showtimes_refactored").document(showtimeId)
                .get()
                .addOnSuccessListener(doc -> {
                    Showtime showtime = doc.toObject(Showtime.class);
                    if (showtime != null) showtime.setId(doc.getId());
                    callback.onSuccess(showtime);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Interface callback
    public interface ShowtimeCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}