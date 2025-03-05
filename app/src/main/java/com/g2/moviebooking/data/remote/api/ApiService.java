package com.g2.moviebooking.data.remote.api;

import com.g2.moviebooking.data.remote.model.GoogleTokenRequest;
import com.g2.moviebooking.data.remote.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/google")
    Call<LoginResponse> loginWithGoogle(@Body GoogleTokenRequest request);
}