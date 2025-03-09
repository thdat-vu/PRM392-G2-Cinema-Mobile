package com.g2.moviebooking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.remote.model.Response.LoginResponse;
import com.g2.moviebooking.data.repository.AuthRepository;
import com.g2.moviebooking.ui.MovieListActivity;
import com.g2.moviebooking.utils.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    // UI components
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnGoogleSignIn;

    // Authentication components
    private AuthRepository authRepository;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo repository
        authRepository = new AuthRepository(this);

        // Khởi tạo UI
        setupViews();

        // Cấu hình Google Sign-In
        setupGoogleSignIn();

        // Gán sự kiện cho các button
        setupListeners();
    }

    private void setupViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        authRepository.login(email, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                handleLoginResponse(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();
                sendTokenToBackend(idToken);
            } catch (ApiException e) {
                showToast("Đăng nhập Google thất bại: " + e.getMessage());
            }
        }
    }

    private void sendTokenToBackend(String idToken) {
        authRepository.loginWithGoogle(idToken, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                handleLoginResponse(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                handleNetworkError(t);
            }
        });
    }

    // Xử lý phản hồi đăng nhập
    private void handleLoginResponse(Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String jwtToken = response.body().getJwtToken();
            RetrofitClient.setToken(this, jwtToken);
            showToast("Đăng nhập thành công!");
            navigateToMovieList();
        } else {
            showToast("Đăng nhập thất bại: " + response.code());
        }
    }

    // Xử lý lỗi mạng
    private void handleNetworkError(Throwable t) {
        showToast("Lỗi mạng: " + t.getMessage());
    }

    // Điều hướng đến MovieListActivity
    private void navigateToMovieList() {
        Intent intent = new Intent(this, MovieListActivity.class);
        startActivity(intent);
        finish();
    }

    // Hiển thị thông báo
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Phương thức logout (nếu cần)
    public void logout() {
        RetrofitClient.clearToken(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}