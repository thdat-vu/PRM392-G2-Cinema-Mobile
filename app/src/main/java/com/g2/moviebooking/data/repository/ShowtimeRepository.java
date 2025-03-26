package com.g2.moviebooking.data.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.utils.FirebaseClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowtimeRepository {
    private final FirebaseFirestore db;
    private static final String TAG = "ShowtimeRepository";

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

    public void updateSeats(String showtimeId, List<String> selectedSeats, ShowtimeCallback<Showtime> callback) {
        // Kiểm tra dữ liệu đầu vào
        if (showtimeId == null || showtimeId.isEmpty()) {
            Log.e(TAG, "Showtime ID is null or empty");
            callback.onFailure("Showtime ID is null or empty");
            return;
        }

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            Log.e(TAG, "Selected seats list is null or empty");
            callback.onFailure("Selected seats list is null or empty");
            return;
        }

        // Log dữ liệu đầu vào
        Log.d(TAG, "Attempting to update seats for showtime: " + showtimeId);
        Log.d(TAG, "Selected seats: " + selectedSeats);

        // Tham chiếu đến document
        DocumentReference showtimeRef = db.collection("showtimes_refactored").document(showtimeId);

        // Sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu
        db.runTransaction((Transaction transaction) -> {
            // Lấy document trong transaction
            Showtime showtime = transaction.get(showtimeRef).toObject(Showtime.class);

            // Gán ID cho showtime
            assert showtime != null;
            showtime.setId(showtimeRef.getId());

            // Khởi tạo danh sách nếu null
            List<String> currentBooked = showtime.getBookedSeats() != null ?
                    new ArrayList<>(showtime.getBookedSeats()) : new ArrayList<>();
            List<String> currentAvailable = showtime.getAvailableSeats() != null ?
                    new ArrayList<>(showtime.getAvailableSeats()) : new ArrayList<>();

            // Log trạng thái hiện tại
            Log.d(TAG, "Current booked seats: " + currentBooked);
            Log.d(TAG, "Current available seats: " + currentAvailable);

            // Cập nhật danh sách ghế
            currentAvailable.removeAll(selectedSeats);
            currentBooked.addAll(selectedSeats);

            // Cập nhật document trong transaction
            transaction.update(showtimeRef, "availableSeats", currentAvailable);
            transaction.update(showtimeRef, "bookedSeats", currentBooked);

            // Cập nhật showtime object để trả về
            showtime.setAvailableSeats(currentAvailable);
            showtime.setBookedSeats(currentBooked);

            return showtime;
        }).addOnSuccessListener(showtime -> {
            Log.d(TAG, "Seats updated successfully for showtime: " + showtime.getId());
            callback.onSuccess(showtime);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to update seats: " + e.getMessage());
            callback.onFailure("Failed to update seats: " + e.getMessage());
        });
    }

    // Interface callback
    public interface ShowtimeCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}