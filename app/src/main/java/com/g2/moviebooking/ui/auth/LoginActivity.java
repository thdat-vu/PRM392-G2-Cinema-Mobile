package com.g2.moviebooking.ui.auth;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo TextView sau khi setContentView
        tvRegister = findViewById(R.id.tv_register);

        String text = "Chưa có tài khoản? Đăng ký ngay";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), text.indexOf("Đăng ký ngay"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(spannable);

        authRepository = new AuthRepository(this);
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);
        Drawable googleIcon = getResources().getDrawable(R.drawable.ic_google);
        googleIcon.setBounds(0, 0, 60, 60); // Điều chỉnh kích thước icon
        btnGoogleSignIn.setCompoundDrawables(googleIcon, null, null, null);
    }

    private void showCustomToast(String message, boolean isSuccess) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.tv_toast_message));

        TextView textView = layout.findViewById(R.id.tv_toast_message);
        ImageView iconView = layout.findViewById(R.id.img_toast_icon);

        textView.setText(message);

        if (isSuccess) {
            iconView.setImageResource(R.drawable.ic_success);
        } else {
            iconView.setImageResource(R.drawable.ic_error);
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
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
                    showCustomToast("Đăng nhập Google thành công!", true);
                    navigateToMovieList();
                }

                @Override
                public void onFailure(String error) {
                    showCustomToast("Đăng nhập Google thất bại: " + error, false);
                }
            });
        } catch (ApiException e) {
            showCustomToast("Google Sign-In thất bại: " + e.getMessage(), false);
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
