package com.g2.moviebooking.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.g2.moviebooking.data.model.Booking.FoodItem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FoodAndDrinkRepository {
    private final FirebaseFirestore db;

    public FoodAndDrinkRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Lấy thông tin chi tiết của một combo dựa trên id
    public CompletableFuture<FoodItem> getFoodItemById(String foodItemId) {
        CompletableFuture<FoodItem> future = new CompletableFuture<>();
        db.collection("foods_drinks")
                .document(foodItemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        double price = documentSnapshot.getDouble("price");
                        // Tạo FoodItem với đầy đủ thông tin
                        FoodItem foodItem = new FoodItem(foodItemId, name, price, 0); // quantity sẽ được set sau
                        future.complete(foodItem);
                    } else {
                        future.completeExceptionally(new Exception("Food item not found"));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    // Lấy danh sách FoodItem dựa trên danh sách id và quantity từ booking
    public CompletableFuture<List<FoodItem>> getFoodItemsForBooking(List<FoodItem> foodItemRefs) {
        CompletableFuture<List<FoodItem>> future = new CompletableFuture<>();
        List<CompletableFuture<FoodItem>> futures = new ArrayList<>();

        for (FoodItem ref : foodItemRefs) {
            CompletableFuture<FoodItem> foodItemFuture = getFoodItemById(ref.getId())
                    .thenApply(foodItem -> {
                        foodItem.setQuantity(ref.getQuantity()); // Gán quantity từ booking
                        return foodItem;
                    });
            futures.add(foodItemFuture);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<FoodItem> foodItems = new ArrayList<>();
                    for (CompletableFuture<FoodItem> f : futures) {
                        try {
                            foodItems.add(f.get());
                        } catch (Exception e) {
                            // Xử lý lỗi nếu cần
                        }
                    }
                    return foodItems;
                })
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        future.completeExceptionally(throwable);
                    } else {
                        future.complete(result);
                    }
                });

        return future;
    }

    public CompletableFuture<List<FoodItem>> getAllFoodAndDrinks() {
        CompletableFuture<List<FoodItem>> future = new CompletableFuture<>();
        
        db.collection("foods_drinks")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<FoodItem> foodItems = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    String id = document.getId();
                    String name = document.getString("name");
                    Double price = document.getDouble("price");
                    String imageUrl = document.getString("imageUrl");
                    
                    if (name != null && price != null) {
                        FoodItem item = new FoodItem(id, name, price, 0);
                        // Set image URL if available
//                        if (imageUrl != null) {
//                            item.setImageUrl(imageUrl);
//                        }
                        foodItems.add(item);
                    }
                }
                future.complete(foodItems);
            })
            .addOnFailureListener(e -> {
                future.completeExceptionally(new Exception("Failed to fetch food and drink items: " + e.getMessage()));
            });
            
        return future;
    }
}