package com.g2.moviebooking.data.remote.model.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Booking {
    @SerializedName("userId")
    private String userId; // ID của người dùng

    @SerializedName("showtimeId")
    private String showtimeId; // ID của suất chiếu

    @SerializedName("seatIds")
    private List<String> seatIds; // Danh sách ID của các ghế được đặt

    @SerializedName("totalPrice")
    private double totalPrice; // Tổng giá tiền

    @SerializedName("status")
    private String status; // Trạng thái đặt vé (ví dụ: "confirmed", "pending")

    // Getter methods
    public String getUserId() { return userId; }
    public String getShowtimeId() { return showtimeId; }
    public List<String> getSeatIds() { return seatIds; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
}