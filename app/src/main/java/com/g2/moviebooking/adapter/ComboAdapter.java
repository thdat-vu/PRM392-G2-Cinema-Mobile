package com.g2.moviebooking.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.model.FoodDrink;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ComboAdapter extends RecyclerView.Adapter<ComboAdapter.ComboViewHolder> {

    private List<FoodDrink> combos;

    public ComboAdapter(List<FoodDrink> combos) {
        this.combos = combos;
    }

    @NonNull
    @Override
    public ComboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_drink_checkout, parent, false);
        return new ComboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboViewHolder holder, int position) {
        FoodDrink combo = combos.get(position);
        holder.bind(combo);
    }

    @Override
    public int getItemCount() {
        return combos.size();
    }

    class ComboViewHolder extends RecyclerView.ViewHolder {

        ImageView ivComboImage;
        TextView tvComboName, tvComboPrice, tvComboQuantity;

        public ComboViewHolder(@NonNull View itemView) {
            super(itemView);
            ivComboImage = itemView.findViewById(R.id.ivComboImage);
            tvComboName = itemView.findViewById(R.id.tvComboName);
            tvComboPrice = itemView.findViewById(R.id.tvComboPrice);
            tvComboQuantity = itemView.findViewById(R.id.tvComboQuantity);
        }

        public void bind(FoodDrink combo) {
            tvComboName.setText(combo.getName());
            // Format the price with commas, then append "đ"
            tvComboPrice.setText(String.format(Locale.getDefault(), "%,dđ", combo.getPrice()));
            tvComboQuantity.setText("x" + combo.getQuantity());

            // If you use an image loading library like Glide or Picasso, load imageUrl here:
            if (combo.getImageUrl() != null && !combo.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext()).load(combo.getImageUrl()).into(ivComboImage);
            }
        }
    }
}

