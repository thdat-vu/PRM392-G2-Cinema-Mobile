package com.g2.moviebooking.data.remote.model.Entity;

import com.google.gson.annotations.SerializedName;

public class Seat {
    @SerializedName("theaterId")
    private String theaterId; // ID của rạp

    @SerializedName("seatNumber")
    private String seatNumber; // Số ghế

    @SerializedName("isAvailable")
    private boolean isAvailable; // Trạng thái ghế (còn trống hay không)

    @SerializedName("bookedBy")
    private String bookedBy; // ID của người đặt (có thể null)

    // Getter methods
    public String getTheaterId() { return theaterId; }
    public String getSeatNumber() { return seatNumber; }
    public boolean isAvailable() { return isAvailable; }
    public String getBookedBy() { return bookedBy; }
}