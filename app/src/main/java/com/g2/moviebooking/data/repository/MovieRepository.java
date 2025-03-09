package com.g2.moviebooking.data.repository;

import android.content.Context;

import com.g2.moviebooking.data.remote.api.ApiService;
import com.g2.moviebooking.data.remote.model.Entity.Movie;
import com.g2.moviebooking.data.remote.model.Response.MovieResponse;
import com.g2.moviebooking.utils.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private ApiService apiService;

    public MovieRepository(Context context) {
        apiService = RetrofitClient.getInstance(context).create(ApiService.class);
    }

    public void getMovies(int pageNum, int pageSize, Callback<List<Movie>> callback) {
        Call<MovieResponse> call = apiService.getMovies(pageNum, pageSize);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Movie> movies = response.body().getData().getPageData();
                    // Tạo một Response mới với kiểu List<Movie>
                    Response<List<Movie>> newResponse = Response.success(movies);
                    // Gọi callback với null cho call vì kiểu không khớp
                    callback.onResponse(null, newResponse);
                } else {
                    // Tạo một Response error
                    Response<List<Movie>> errorResponse = Response.error(response.code(), response.errorBody());
                    callback.onResponse(null, errorResponse);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Gọi callback với null cho call vì kiểu không khớp
                callback.onFailure(null, t);
            }
        });
    }
}