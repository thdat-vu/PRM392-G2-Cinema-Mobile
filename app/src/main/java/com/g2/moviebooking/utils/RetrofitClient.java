package com.g2.moviebooking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://apimocha.com/wakiwaki2922/";

    public static synchronized Retrofit getInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("jwt_token", null);
                        Request.Builder requestBuilder = chain.request().newBuilder();
                        if (token != null) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }
                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // Reset Retrofit khi cần (ví dụ: khi token thay đổi)
    public static void reset() {
        retrofit = null;
    }
}