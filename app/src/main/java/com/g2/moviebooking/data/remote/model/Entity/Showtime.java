package com.g2.moviebooking.data.remote.model.Entity;

import com.google.gson.annotations.SerializedName;

public class Showtime {
    @SerializedName("movieId")
    private String movieId; // ID của phim (dùng String thay vì ObjectId)

    @SerializedName("theaterId")
    private String theaterId; // ID của rạp (dùng String thay vì ObjectId)

    @SerializedName("startTime")
    private String startTime; // Thời gian bắt đầu (có thể đổi thành Date nếu cần)

    @SerializedName("endTime")
    private String endTime; // Thời gian kết thúc (có thể đổi thành Date nếu cần)

    // Getter methods
    public String getMovieId() { return movieId; }
    public String getTheaterId() { return theaterId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}