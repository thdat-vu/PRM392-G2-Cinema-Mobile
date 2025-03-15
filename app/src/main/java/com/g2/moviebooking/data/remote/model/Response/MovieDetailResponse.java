package com.g2.moviebooking.data.remote.model.Response;

import com.g2.moviebooking.data.remote.model.Entity.Movie;
import com.google.gson.annotations.SerializedName;

public class MovieDetailResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Movie data;

    // Getter methods
    public boolean isSuccess() {
        return success;
    }

    public Movie getData() {
        return data;
    }
}