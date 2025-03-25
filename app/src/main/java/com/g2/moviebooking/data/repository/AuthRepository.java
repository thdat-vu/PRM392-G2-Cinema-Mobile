package com.g2.moviebooking.data.repository;

import android.content.Context;
import android.util.Log;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.g2.moviebooking.data.model.User;
import com.g2.moviebooking.utils.FirebaseClient;

public class AuthRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private static final String TAG = "AuthRepository";

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
                            saveUserToFirestore(user, user.getDisplayName(), callback);
                        } else {
                            callback.onFailure("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        callback.onFailure("Đăng nhập thất bại: " + task.getException().getMessage());
                    }
                });
    }

    // Đăng nhập bằng email và mật khẩu
    public void loginWithEmail(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user, user.getDisplayName(), callback);
                        } else {
                            callback.onFailure("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Đăng ký bằng email và mật khẩu với name
    public void registerWithEmail(String email, String password, String name, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Registering user with name: " + name);
                            updateUserProfileAndFirestore(user, name, callback);
                        } else {
                            callback.onFailure("Không thể lấy thông tin người dùng");
                        }
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Helper method để cập nhật profile và lưu thông tin user vào Firestore
    private void updateUserProfileAndFirestore(FirebaseUser user, String name, AuthCallback callback) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(profileTask -> {
                    if (profileTask.isSuccessful()) {
                        User newUser = new User(name, user.getEmail());
                        db.collection("users").document(user.getUid())
                                .set(newUser)
                                .addOnCompleteListener(setTask -> {
                                    if (setTask.isSuccessful()) {
                                        Log.d(TAG, "User saved to Firestore with name: " + name);
                                        callback.onSuccess(user);
                                    } else {
                                        callback.onFailure("Lưu người dùng thất bại: " + setTask.getException().getMessage());
                                    }
                                });
                    } else {
                        callback.onFailure("Cập nhật profile thất bại: " + profileTask.getException().getMessage());
                    }
                });
    }

    // Helper method để lưu thông tin user vào Firestore (dùng cho login)
    private void saveUserToFirestore(FirebaseUser user, String name, AuthCallback callback) {
        String displayName = (name != null && !name.isEmpty()) ? name : "Người dùng chưa đặt tên";
        User newUser = new User(displayName, user.getEmail());
        db.collection("users").document(user.getUid())
                .set(newUser)
                .addOnCompleteListener(setTask -> {
                    if (setTask.isSuccessful()) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("Lưu người dùng thất bại: " + setTask.getException().getMessage());
                    }
                });
    }

    // Interface callback để xử lý kết quả
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String error);
    }
}