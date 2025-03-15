package com.g2.moviebooking.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.repository.AuthRepository;
import com.g2.moviebooking.ui.MovieListActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    private Button btnGoogleSignIn;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository(this);
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);
    }

    private void setupListeners() {
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    private void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            authRepository.loginWithGoogle(account.getIdToken(), new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    showToast("Đăng nhập Google thành công!");
                    navigateToMovieList();
                }

                @Override
                public void onFailure(String error) {
                    showToast("Đăng nhập Google thất bại: " + error);
                }
            });
        } catch (ApiException e) {
            showToast("Google Sign-In thất bại: " + e.getMessage());
        }
    }

    private void navigateToMovieList() {
        startActivity(new Intent(this, MovieListActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}