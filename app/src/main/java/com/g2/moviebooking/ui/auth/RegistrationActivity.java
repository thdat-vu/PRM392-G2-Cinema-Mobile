package com.g2.moviebooking.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Response.RegistrationResponse;
import com.g2.moviebooking.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    // UI components
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPhone;
    private Button btnRegister;
    private Button btnBackToLogin;

    // Repository
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Khởi tạo repository
        authRepository = new AuthRepository(this);

        // Khởi tạo UI
        setupViews();

        // Gán sự kiện cho các button
        setupListeners();
    }

    private void setupViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> register());
        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void register() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        authRepository.register(name, email, password, phone, new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                handleRegistrationResponse(response);
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // Xử lý phản hồi đăng ký
    private void handleRegistrationResponse(Response<RegistrationResponse> response) {
        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
            showToast("Đăng ký thành công! Vui lòng đăng nhập");
            finish();
        } else {
            String message = response.body() != null ? response.body().getMessage() : "Đăng ký thất bại: " + response.code();
            showToast(message);
        }
    }

    // Hiển thị thông báo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}