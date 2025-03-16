package com.g2.moviebooking.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Showtime implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("movieId")
    private String movieId;

    @SerializedName("movie")
    private Movie movie;

    @SerializedName("theatreId")
    private String theatreId;

    @SerializedName("theatre")
    private Theatre theatre;

    @SerializedName("screenId")
    private String screenId;

    @SerializedName("screenName")
    private String screenName;

    @SerializedName("startTime")
    private Date startTime;

    @SerializedName("endTime")
    private Date endTime;

    @SerializedName("date")
    private Date date;

    @SerializedName("format")
    private String format;

    @SerializedName("language")
    private String language;

    @SerializedName("price")
    private double price;

    @SerializedName("availableSeats")
    private List<String> availableSeats;

    @SerializedName("bookedSeats")
    private List<String> bookedSeats;

    // Constructors
    public Showtime() {
    }

    public Showtime(String id, String movieId, Movie movie, String theatreId, Theatre theatre,
                   String screenId, String screenName, Date startTime, Date endTime, Date date,
                   String format, String language, double price, List<String> availableSeats,
                   List<String> bookedSeats) {
        this.id = id;
        this.movieId = movieId;
        this.movie = movie;
        this.theatreId = theatreId;
        this.theatre = theatre;
        this.screenId = screenId;
        this.screenName = screenName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.format = format;
        this.language = language;
        this.price = price;
        this.availableSeats = availableSeats;
        this.bookedSeats = bookedSeats;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(String theatreId) {
        this.theatreId = theatreId;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public String getFormattedShowtime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH); // 12-hour format with AM/PM

        return String.format("%s | %s - %s",
                dateFormat.format(date),
                timeFormat.format(startTime),
                timeFormat.format(endTime));
    }
}
