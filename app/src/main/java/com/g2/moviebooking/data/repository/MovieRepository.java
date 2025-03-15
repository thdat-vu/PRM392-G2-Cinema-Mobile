package com.g2.moviebooking.data.repository;

import android.content.Context;

import com.g2.moviebooking.data.remote.api.ApiService;
import com.g2.moviebooking.data.remote.model.Response.MovieDetailResponse;
import com.g2.moviebooking.data.remote.model.Response.MovieResponse;
import com.g2.moviebooking.utils.RetrofitClient;

import retrofit2.Call;

public class MovieRepository {
    private final ApiService apiService;

    public MovieRepository(Context context) {
        apiService = RetrofitClient.getInstance(context).create(ApiService.class);
    }

    // Lấy danh sách phim với phân trang
    public Call<MovieResponse> getMovies(int pageNum, int pageSize) {
        return apiService.getMovies(pageNum, pageSize);
    }

    // Lấy thông tin chi tiết của một phim
    public Call<MovieDetailResponse> getMovieDetail(String movieId) {
        return apiService.getMovieDetail(movieId);
    }
}