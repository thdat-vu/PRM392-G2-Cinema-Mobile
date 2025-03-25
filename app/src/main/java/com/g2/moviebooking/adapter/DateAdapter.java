package com.g2.moviebooking.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.g2.moviebooking.R;

import java.util.Calendar;
import java.util.List;

import java.util.Date;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    public interface OnDateClickListener {
        void onDateClicked(Date date, int position);
    }

    private List<Date> dateList;
    private OnDateClickListener listener;
    private int selectedPosition = 0;

    public DateAdapter(List<Date> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
        Date date = dateList.get(position);

        // Lấy thông tin ngày trong tuần bằng Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (isToday(date)) {
            holder.tvDayOfWeek.setText("H.nay");
        } else {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // Nếu là Chủ Nhật (Sunday)
            if (dayOfWeek == Calendar.SUNDAY) {
                holder.tvDayOfWeek.setText("CN");
            } else {
                holder.tvDayOfWeek.setText("Thứ " + dayOfWeek);
            }
        }
        // Hiển thị số ngày, ví dụ "25"
        holder.tvDayNumber.setText(String.format(Locale.getDefault(), "%td", date));

        // Update the backgrounds and text colors based on whether the item is selected
        if (position == selectedPosition) {
            // Selected state:
            // Outer container gets pink stroke border
            holder.container.setBackgroundResource(R.drawable.bg_container_selected);

            // Top layout (day-of-week) stays white
            holder.llDayOfWeek.setBackgroundColor(Color.WHITE);

            // Bottom layout is pink
            holder.llDayNumber.setBackgroundResource(R.drawable.bg_date_selected);

            // Adjust text colors as needed
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#E91E63"));
            holder.tvDayNumber.setTextColor(Color.WHITE);
        } else {
            // Unselected state: reset inner layouts and container background
            holder.llDayOfWeek.setBackgroundColor(Color.TRANSPARENT);
            holder.llDayNumber.setBackgroundResource(R.drawable.bg_date_unselected);
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#0F0E0E"));
            holder.tvDayNumber.setTextColor(Color.BLACK);
            holder.container.setBackgroundResource(R.drawable.bg_date_unselected);
        }

        // Xử lý sự kiện nhấn
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPos);
            notifyItemChanged(position);
            if (listener != null) {
                listener.onDateClicked(date, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar given = Calendar.getInstance();
        given.setTime(date);
        return (today.get(Calendar.YEAR) == given.get(Calendar.YEAR)) &&
                (today.get(Calendar.DAY_OF_YEAR) == given.get(Calendar.DAY_OF_YEAR));
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        LinearLayout llDayOfWeek, llDayNumber;
        TextView tvDayOfWeek, tvDayNumber;

        public DateViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.date_item_container);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            llDayOfWeek = itemView.findViewById(R.id.ll_day_of_week);
            llDayNumber = itemView.findViewById(R.id.ll_day_number);
        }
    }
}
