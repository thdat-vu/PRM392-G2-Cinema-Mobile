package com.g2.moviebooking.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.g2.moviebooking.ui.auth.LoginActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://prm-392-g2-cinema.vercel.app/";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOKEN_KEY = "jwt_token";

    private static volatile Retrofit retrofit;
    private static volatile OkHttpClient okHttpClient;
    private static Context appContext;

    private RetrofitClient() {
        // Ngăn khởi tạo instance
    }

    // Khởi tạo Retrofit instance
    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    appContext = context.getApplicationContext();
                    okHttpClient = buildOkHttpClient(appContext);
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
                }
            }
        }
        return retrofit;
    }

    // Tạo OkHttpClient với interceptor và authenticator
    private static OkHttpClient buildOkHttpClient(Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .authenticator(new TokenAuthenticator(context))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // Quản lý token
    public static void setToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(TOKEN_KEY, token).apply();
        reset();
    }

    private static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }

    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(TOKEN_KEY).apply();
        reset();
    }

    public static void reset() {
        retrofit = null;
        okHttpClient = null;
    }

    public static boolean hasToken(Context context) {
        return getToken(context) != null;
    }

    // Interceptor để thêm token vào header
    private static class AuthInterceptor implements Interceptor {
        private final Context context;

        AuthInterceptor(Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String token = getToken(context);

            Request modifiedRequest = token != null
                    ? originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build()
                    : originalRequest;

            Response response = chain.proceed(modifiedRequest);
            if (response.code() == 401) {
                navigateToLogin();
            }
            return response;
        }
    }

    // Authenticator để xử lý khi token hết hạn
    private static class TokenAuthenticator implements Authenticator {
        private final Context context;

        TokenAuthenticator(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public Request authenticate(Route route, @NonNull Response response) {
            synchronized (this) {
                String currentToken = getToken(context);
                if (currentToken != null) {
                    clearToken(context);
                    navigateToLogin();
                }
            }
            return null;
        }
    }

    // Điều hướng về LoginActivity
    private static void navigateToLogin() {
        if (appContext != null) {
            Intent intent = new Intent(appContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appContext.startActivity(intent);
        }
    }
}