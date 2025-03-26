package com.g2.moviebooking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.g2.moviebooking.R;
import com.g2.moviebooking.ui.auth.LoginActivity;
import com.g2.moviebooking.ui.bookings.BookingHistoryListActivity;
import com.g2.moviebooking.ui.tickets.TicketsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvGreeting, tvEmail;
    private EditText etName, etPhone;
    private Button btnUpdate, btnLogout;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupViews();
        setupListeners();
        loadUserData();
        setupBottomNavigation();
    }

    private void setupViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvEmail = findViewById(R.id.tv_email);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        btnUpdate = findViewById(R.id.btn_update);
        btnLogout = findViewById(R.id.btn_logout);
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
        btnUpdate.setOnClickListener(v -> updateUserInfo());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());

            // Lấy thông tin từ Firestore
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String phone = document.getString("phone");

                                // Cập nhật lời chào với tên người dùng
                                tvGreeting.setText("Chào " + (name != null ? name : "Chưa cập nhật"));
                                etName.setText(name != null ? name : "Chưa cập nhật");
                                etPhone.setText(phone != null ? phone : "Chưa cập nhật");
                            }
                        } else {
                            showCustomToast("Không thể tải thông tin: " + task.getException().getMessage(), false);
                        }
                    });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void updateUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            showCustomToast("Vui lòng đăng nhập lại", false);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String newName = etName.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        // Validate input
        if (newName.isEmpty()) {
            showCustomToast("Tên không được để trống", false);
            return;
        }
        if (newPhone.isEmpty()) {
            showCustomToast("Số điện thoại không được để trống", false);
            return;
        }
        if (!android.util.Patterns.PHONE.matcher(newPhone).matches() || newPhone.length() < 10) {
            showCustomToast("Số điện thoại không hợp lệ (ít nhất 10 số)", false);
            return;
        }

        // Cập nhật Firestore
        db.collection("users")
                .document(currentUser.getUid())
                .update("name", newName, "phone", newPhone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showCustomToast("Cập nhật thông tin thành công", true);
                        tvGreeting.setText("Chào " + newName); // Cập nhật lời chào
                    } else {
                        showCustomToast("Cập nhật thất bại: " + task.getException().getMessage(), false);
                    }
                });

        // Cập nhật displayName trong FirebaseAuth
        currentUser.updateProfile(new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build())
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showCustomToast("Cập nhật tên trên FirebaseAuth thất bại: " + task.getException().getMessage(), false);
                    }
                });
    }

    private void logout() {
        auth.signOut();
        showCustomToast("Đã đăng xuất", true);
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_movies) {
                startActivity(new Intent(this, MovieListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_theatres) {
                showCustomToast("Chức năng rạp chưa được triển khai", false);
                return true;
            } else if (itemId == R.id.nav_tickets) {
                startActivity(new Intent(this, BookingHistoryListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
}