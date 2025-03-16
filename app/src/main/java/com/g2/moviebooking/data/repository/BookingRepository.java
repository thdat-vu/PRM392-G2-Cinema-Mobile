package com.g2.moviebooking.data.repository;

import com.g2.moviebooking.data.model.Booking;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class BookingRepository {
    private final FirebaseFirestore db;

    public BookingRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy tất cả bookings của user
    public void getBookingsByUserId(String userId, BookingCallback<List<Booking>> callback) {
        db.collection("bookings_refactored")
                .whereEqualTo("userId", userId)
                .orderBy("bookingDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Booking> bookings = querySnapshot.toObjects(Booking.class);
                    for (int i = 0; i < bookings.size(); i++) {
                        bookings.get(i).setId(querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(bookings);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Thêm booking mới
    public void addBooking(Booking booking, BookingCallback<String> callback) {
        db.collection("bookings_refactored")
                .add(booking)
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Update trạng thái booking (ví dụ: cancel, confirmed)
    public void updateBookingStatus(String bookingId, String status, BookingCallback<Void> callback) {
        db.collection("bookings_refactored").document(bookingId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Callback Interface
    public interface BookingCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}
