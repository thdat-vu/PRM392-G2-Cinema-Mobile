package com.g2.moviebooking.ui.auth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private AuthRepository authRepository;
    private boolean isPasswordVisible = false; // Trạng thái chung cho cả hai trường

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository(this);
        setupViews();
        setupListeners();
    }

    private void setupViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // Thiết lập biểu tượng ban đầu cho cả hai trường (ẩn)
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);

        String text = "Đã có tài khoản? Đăng nhập ngay";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), text.indexOf("Đăng nhập ngay"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(spannable);
    }

    private void showCustomToast(String message, boolean isSuccess) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.tv_toast_message));

        TextView textView = layout.findViewById(R.id.tv_toast_message);
        ImageView iconView = layout.findViewById(R.id.img_toast_icon);

        textView.setText(message);
        iconView.setImageResource(isSuccess ? R.drawable.ic_success : R.drawable.ic_error);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInput(name, email, password, confirmPassword)) {
                registerWithEmail(name, email, password);
            }
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Sự kiện nhấn vào biểu tượng của etPassword
        etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        // Sự kiện nhấn vào biểu tượng của etConfirmPassword
        etConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        if (!isPasswordVisible) {
            // Hiện cả hai mật khẩu
            etPassword.setTransformationMethod(null);
            etConfirmPassword.setTransformationMethod(null);
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
            etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
        } else {
            // Ẩn cả hai mật khẩu
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
            etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        // Giữ con trỏ ở cuối cho cả hai trường
        etPassword.setSelection(etPassword.getText().length());
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.trim().isEmpty()) {
            showCustomToast("Tên không được để trống", false);
            return false;
        }
        if (email.trim().isEmpty()) {
            showCustomToast("Email không được để trống", false);
            return false;
        }
        if (password.trim().isEmpty()) {
            showCustomToast("Mật khẩu không được để trống", false);
            return false;
        }
        if (confirmPassword.trim().isEmpty()) {
            showCustomToast("Xác nhận mật khẩu không được để trống", false);
            return false;
        }

        if (name.length() < 2) {
            showCustomToast("Tên phải dài ít nhất 2 ký tự", false);
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showCustomToast("Email không hợp lệ", false);
            return false;
        }

        if (password.length() < 6) {
            showCustomToast("Mật khẩu phải dài ít nhất 6 ký tự", false);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showCustomToast("Mật khẩu không khớp", false);
            return false;
        }

        return true;
    }

    private void registerWithEmail(String name, String email, String password) {
        authRepository.registerWithEmail(email, password, name, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                showCustomToast("Đăng ký thành công!", true);
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(String error) {
                showCustomToast("Đăng ký thất bại: " + error, false);
            }
        });
    }
}