package com.g2.moviebooking.data.remote.api;

import com.g2.moviebooking.data.remote.model.Request.GoogleTokenRequest;
import com.g2.moviebooking.data.remote.model.Request.LoginRequest;
import com.g2.moviebooking.data.remote.model.Request.RegistrationRequest;
import com.g2.moviebooking.data.remote.model.Response.LoginResponse;
import com.g2.moviebooking.data.remote.model.Response.MovieDetailResponse;
import com.g2.moviebooking.data.remote.model.Response.MovieResponse;
import com.g2.moviebooking.data.remote.model.Response.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/google")
    Call<LoginResponse> loginWithGoogle(@Body GoogleTokenRequest request);

    @GET("api/movies")
    Call<MovieResponse> getMovies(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize
    );

    @POST("api/users")
    Call<RegistrationResponse> register(@Body RegistrationRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // API lấy thông tin chi tiết phim
    @GET("api/movies/{id}")
    Call<MovieDetailResponse> getMovieDetail(@Path("id") String movieId);
}