package com.g2.moviebooking.data.repository;

import android.content.Context;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.g2.moviebooking.data.model.User;
import com.g2.moviebooking.utils.FirebaseClient;

public class AuthRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository(Context context) {
        auth = FirebaseClient.getAuth();
        db = FirebaseClient.getFirestore();
    }

    // Đăng nhập bằng Google
    public void loginWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            User newUser = new User(user.getDisplayName(), user.getEmail());
                            db.collection("users").document(user.getUid())
                                    .set(newUser)
                                    .addOnCompleteListener(setTask -> {
                                        if (setTask.isSuccessful()) {
                                            callback.onSuccess(user);
                                        } else {
                                            callback.onFailure("Lưu người dùng thất bại: " + setTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            callback.onFailure("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        callback.onFailure("Đăng nhập thất bại: " + task.getException().getMessage());
                    }
                });
    }

    // Interface callback để xử lý kết quả
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String error);
    }
}