package com.g2.moviebooking.data.remote.model.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Screen {
    @SerializedName("name")
    private String name;

    @SerializedName("capacity")
    private String capacity; // Sức chứa của phòng chiếu

    @SerializedName("seatIds")
    private List<String> seatIds; // Danh sách ID của các ghế

    // Getter methods
    public String getName() { return name; }
    public String getCapacity() { return capacity; }
    public List<String> getSeatIds() { return seatIds; }
}