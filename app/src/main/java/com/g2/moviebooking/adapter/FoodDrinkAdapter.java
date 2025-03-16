package com.g2.moviebooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.model.FoodDrink;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodDrinkAdapter extends RecyclerView.Adapter<FoodDrinkAdapter.FoodDrinkViewHolder> {

    private List<FoodDrink> foodDrinkList;
    private Context context;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged(List<FoodDrink> foodDrinkList);
    }

    public FoodDrinkAdapter(Context context, List<FoodDrink> foodDrinkList, OnQuantityChangeListener listener) {
        this.context = context;
        this.foodDrinkList = foodDrinkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_drink, parent, false);
        return new FoodDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodDrinkViewHolder holder, int position) {
        FoodDrink foodDrink = foodDrinkList.get(position);
        
        holder.tvFoodDrinkName.setText(foodDrink.getName());
        
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        holder.tvFoodDrinkPrice.setText(currencyFormat.format(foodDrink.getPrice()) + "đ");
        holder.tvQuantity.setText(String.valueOf(foodDrink.getQuantity()));
        
        // Load image using Glide
        if (foodDrink.getImageUrl() != null && !foodDrink.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(foodDrink.getImageUrl())
                    .placeholder(R.drawable.popcorn_combo)
                    .error(R.drawable.popcorn_combo)
                    .into(holder.imgFoodDrink);
        } else {
            holder.imgFoodDrink.setImageResource(R.drawable.popcorn_combo);
        }
        
        // Set click listeners for quantity buttons
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = foodDrink.getQuantity();
            if (currentQuantity > 0) {
                foodDrink.setQuantity(currentQuantity - 1);
                holder.tvQuantity.setText(String.valueOf(foodDrink.getQuantity()));
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onQuantityChanged(foodDrinkList);
                }
            }
        });
        
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = foodDrink.getQuantity();
            foodDrink.setQuantity(currentQuantity + 1);
            holder.tvQuantity.setText(String.valueOf(foodDrink.getQuantity()));
            notifyItemChanged(position);
            if (listener != null) {
                listener.onQuantityChanged(foodDrinkList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodDrinkList.size();
    }

    public static class FoodDrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoodDrink;
        TextView tvFoodDrinkName, tvFoodDrinkPrice, tvQuantity;
        ImageButton btnDecrease, btnIncrease;

        public FoodDrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoodDrink = itemView.findViewById(R.id.imgFoodDrink);
            tvFoodDrinkName = itemView.findViewById(R.id.tvFoodDrinkName);
            tvFoodDrinkPrice = itemView.findViewById(R.id.tvFoodDrinkPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}