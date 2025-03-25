package com.g2.moviebooking.data.repository;

import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
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

    // Tạo booking mới với đầy đủ thông tin
    public void createBooking(String userId, String showtimeId, List<String> selectedSeats,
                              List<Booking.FoodItem> foodItems, String paymentMethod,
                              BookingCallback<Booking> callback) {
        // Tạo bookingCode ngẫu nhiên
        String bookingCode = "BK" + System.currentTimeMillis();
        Date bookingDate = new Date();

        // Lấy thông tin showtime để tính totalAmount
        db.collection("showtimes_refactored").document(showtimeId)
                .get()
                .addOnSuccessListener(doc -> {
                    Showtime showtime = doc.toObject(Showtime.class);
                    if (showtime != null) {
                        showtime.setId(doc.getId());

                        // Tính totalAmount
                        double seatPrice = showtime.getPrice() * selectedSeats.size();
                        double foodPrice = foodItems != null ?
                                foodItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() : 0;
                        double totalAmount = seatPrice + foodPrice;

                        // Tạo booking object
                        Booking booking = new Booking(
                                null, // id sẽ được Firestore tạo tự động
                                userId,
                                showtimeId,
                                showtime,
                                selectedSeats,
                                totalAmount,
                                bookingCode,
                                bookingDate,
                                paymentMethod,
                                "PENDING", // paymentStatus ban đầu
                                null, // transactionId
                                null, // transactionTime
                                "PENDING", // status ban đầu
                                foodItems
                        );

                        // Thêm vào Firestore
                        db.collection("bookings_refactored")
                                .add(booking)
                                .addOnSuccessListener(documentReference -> {
                                    booking.setId(documentReference.getId());
                                    callback.onSuccess(booking);
                                })
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure("Showtime not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Cập nhật trạng thái thanh toán sau khi hoàn tất
    public void confirmPayment(String bookingId, String transactionId,
                               BookingCallback<Booking> callback) {
        Date transactionTime = new Date();
        db.collection("bookings_refactored").document(bookingId)
                .get()
                .addOnSuccessListener(doc -> {
                    Booking booking = doc.toObject(Booking.class);
                    if (booking != null) {
                        booking.setId(doc.getId());

                        // Cập nhật thông tin thanh toán
                        db.collection("bookings_refactored").document(bookingId)
                                .update(
                                        "paymentStatus", "PAID",
                                        "transactionId", transactionId,
                                        "transactionTime", transactionTime,
                                        "status", "CONFIRMED"
                                )
                                .addOnSuccessListener(aVoid -> {
                                    booking.setPaymentStatus("PAID");
                                    booking.setTransactionId(transactionId);
                                    booking.setTransactionTime(transactionTime);
                                    booking.setStatus("CONFIRMED");
                                    callback.onSuccess(booking);
                                })
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        callback.onFailure("Booking not found");
                    }
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
