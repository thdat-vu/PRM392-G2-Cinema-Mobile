package com.g2.moviebooking.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Booking implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("showtimeId")
    private String showtimeId;

    @SerializedName("showtime")
    private Showtime showtime;

    @SerializedName("seats")
    private List<String> seats;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("bookingCode")
    private String bookingCode;

    @SerializedName("bookingDate")
    private Date bookingDate;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("transactionTime")
    private Date transactionTime;

    @SerializedName("status")
    private String status;

    @SerializedName("foodItems")
    private List<FoodItem> foodItems;

    // Constructors
    public Booking() {
    }

    public Booking(String id, String userId, String showtimeId, Showtime showtime, List<String> seats,
                  double totalAmount, String bookingCode, Date bookingDate, String paymentMethod,
                  String paymentStatus, String transactionId, Date transactionTime, String status,
                  List<FoodItem> foodItems) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.showtime = showtime;
        this.seats = seats;
        this.totalAmount = totalAmount;
        this.bookingCode = bookingCode;
        this.bookingDate = bookingDate;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.transactionTime = transactionTime;
        this.status = status;
        this.foodItems = foodItems;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    // Inner class for food items
    public static class FoodItem implements Serializable {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("price")
        private double price;

        @SerializedName("quantity")
        private int quantity;

        public FoodItem() {
        }

        public FoodItem(String id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
