package com.g2.moviebooking.data.repository;

import android.content.Context;
import com.g2.moviebooking.data.remote.api.ApiService;
import com.g2.moviebooking.data.remote.model.Movie;
import com.g2.moviebooking.utils.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import java.util.List;

public class MovieRepository {
    private ApiService apiService;

    public MovieRepository(Context context) {
        apiService = RetrofitClient.getInstance(context).create(ApiService.class);
    }

    public void getMovies(Callback<List<Movie>> callback) {
        Call<List<Movie>> call = apiService.getMovies();
        call.enqueue(callback);
    }
}