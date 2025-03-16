package com.g2.moviebooking.data.repository;

import com.g2.moviebooking.data.model.Theatre;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TheatreRepository {
    private final FirebaseFirestore db;

    public TheatreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy tất cả theatres
    public void getAllTheatres(TheatreCallback<List<Theatre>> callback) {
        db.collection("theatres")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Theatre> theatres = querySnapshot.toObjects(Theatre.class);
                    for (int i = 0; i < theatres.size(); i++) {
                        theatres.get(i).setId(querySnapshot.getDocuments().get(i).getId());
                    }
                    callback.onSuccess(theatres);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Lấy chi tiết theatre
    public void getTheatreDetail(String theatreId, TheatreCallback<Theatre> callback) {
        db.collection("theatres").document(theatreId)
                .get()
                .addOnSuccessListener(doc -> {
                    Theatre theatre = doc.toObject(Theatre.class);
                    if (theatre != null) theatre.setId(doc.getId());
                    callback.onSuccess(theatre);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Callback interface
    public interface TheatreCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}
