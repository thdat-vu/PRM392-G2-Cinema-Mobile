package com.g2.moviebooking.data.repository;

import com.g2.moviebooking.data.model.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.type.Date;

import java.util.ArrayList;
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

    // Lấy tất cả showtimes theo movieId và date
    public void getShowtimesByMovieIdAndDate(String movieId, Date date, ShowtimeCallback<List<Showtime>> callback) {
        db.collection("showtimes_refactored")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("date", date)
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

    // Thêm method để update seats
    public void updateSeats(String showtimeId, List<String> selectedSeats,
                            ShowtimeCallback<Showtime> callback) {
        // Lấy document reference
        db.collection("showtimes_refactored").document(showtimeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Showtime showtime = documentSnapshot.toObject(Showtime.class);
                    if (showtime != null) {
                        showtime.setId(documentSnapshot.getId());

                        // Kiểm tra các ghế đã chọn có trong bookedSeats không
                        List<String> alreadyBooked = new ArrayList<>();
                        List<String> currentBooked = showtime.getBookedSeats() != null ?
                                showtime.getBookedSeats() : new ArrayList<>();
                        List<String> currentAvailable = showtime.getAvailableSeats() != null ?
                                showtime.getAvailableSeats() : new ArrayList<>();

                        for (String seat : selectedSeats) {
                            if (currentBooked.contains(seat)) {
                                alreadyBooked.add(seat);
                            }
                        }

                        // Nếu có ghế đã được đặt, trả về lỗi
                        if (!alreadyBooked.isEmpty()) {
                            String errorMessage = "The following seats are already booked: " +
                                    String.join(", ", alreadyBooked);
                            callback.onFailure(errorMessage);
                            return;
                        }

                        // Cập nhật danh sách ghế
                        currentAvailable.removeAll(selectedSeats);
                        currentBooked.addAll(selectedSeats);

                        // Update Firestore
                        db.collection("showtimes_refactored").document(showtimeId)
                                .update(
                                        "availableSeats", currentAvailable,
                                        "bookedSeats", currentBooked
                                )
                                .addOnSuccessListener(aVoid -> {
                                    showtime.setAvailableSeats(currentAvailable);
                                    showtime.setBookedSeats(currentBooked);
                                    callback.onSuccess(showtime);
                                })
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure("Showtime not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Interface callback
    public interface ShowtimeCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}