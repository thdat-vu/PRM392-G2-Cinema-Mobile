package com.g2.moviebooking.data.repository;

import android.content.Context;

import com.g2.moviebooking.data.remote.api.ApiService;
import com.g2.moviebooking.data.remote.model.Request.GoogleTokenRequest;
import com.g2.moviebooking.data.remote.model.Request.LoginRequest;
import com.g2.moviebooking.data.remote.model.Request.RegistrationRequest;
import com.g2.moviebooking.data.remote.model.Response.LoginResponse;
import com.g2.moviebooking.data.remote.model.Response.RegistrationResponse;
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

    public void register(String name, String email, String password, String phoneNumber,
                         Callback<RegistrationResponse> callback) {
        RegistrationRequest request = new RegistrationRequest(name, email, password, phoneNumber);
        Call<RegistrationResponse> call = apiService.register(request);
        call.enqueue(callback);
    }

    public void login(String email, String password, Callback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(request);
        call.enqueue(callback);
    }
}