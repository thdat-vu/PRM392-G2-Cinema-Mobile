package com.g2.moviebooking.data.remote.model.Entity;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("genres")
    private String[] genres;

    @SerializedName("releaseDate")
    private String releaseDate;

    @SerializedName("duration")
    private int duration;

    @SerializedName("director")
    private String director;

    @SerializedName("actors")
    private String[] actors;

    @SerializedName("rating")
    private float rating;

    @SerializedName("banner")
    private String banner;

    @SerializedName("trailer")
    private String trailer;

    // Getter methods
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String[] getGenres() { return genres; }
    public String getReleaseDate() { return releaseDate; }
    public int getDuration() { return duration; }
    public String getDirector() { return director; }
    public String[] getActors() { return actors; }
    public float getRating() { return rating; }
    public String getBanner() { return banner; }
    public String getTrailer() { return trailer; }
}