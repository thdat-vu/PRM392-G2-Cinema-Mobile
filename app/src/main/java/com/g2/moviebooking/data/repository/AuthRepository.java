package com.g2.moviebooking.data.repository;

import android.content.Context;
import com.g2.moviebooking.data.remote.api.ApiService;
import com.g2.moviebooking.data.remote.model.GoogleTokenRequest;
import com.g2.moviebooking.data.remote.model.LoginResponse;
import com.g2.moviebooking.utils.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository(Context context) {
        apiService = RetrofitClient.getInstance(context).create(ApiService.class);
    }

    public void loginWithGoogle(String idToken, Callback<LoginResponse> callback) {
        GoogleTokenRequest request = new GoogleTokenRequest(idToken);
        Call<LoginResponse> call = apiService.loginWithGoogle(request);
        call.enqueue(callback);
    }
}