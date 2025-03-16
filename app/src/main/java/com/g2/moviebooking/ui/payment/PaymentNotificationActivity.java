package com.g2.moviebooking.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.ui.MovieListActivity;
import com.g2.moviebooking.ui.tickets.TicketsActivity;

public class PaymentNotificationActivity extends AppCompatActivity {
    TextView tvNotify;
    TextView tvTotal;
    TextView tvBookingCode;
    Button btnReturn;
    Button btnViewTickets;
    ImageView imgPaymentStatus;
    
    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        tvNotify = findViewById(R.id.tvNotify);
        tvTotal = findViewById(R.id.tvTotal);
        tvBookingCode = findViewById(R.id.tvBookingCode);
        btnReturn = findViewById(R.id.btnReturn);
        btnViewTickets = findViewById(R.id.btnViewTickets);
        imgPaymentStatus = findViewById(R.id.imgPaymentStatus);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        tvNotify.setText(result);
        
        // Get booking information
        booking = (Booking) intent.getSerializableExtra("booking");
        String bookingCode = booking.getBookingCode();
        
        // Display booking code if payment was successful
        if (booking != null && "Confirmed".equals(booking.getStatus())) {
            tvBookingCode.setVisibility(View.VISIBLE);
            tvBookingCode.setText("Mã đặt vé: " + bookingCode);
            btnViewTickets.setVisibility(View.VISIBLE);
        } else {
            tvBookingCode.setVisibility(View.GONE);
            btnViewTickets.setVisibility(View.GONE);
        }

        String totalText = intent.getStringExtra("total");
        if (totalText != null) {
            tvTotal.setText(totalText);
        } else {
            tvTotal.setText("");
        }

        assert result != null;
        if (result.equals("Thanh toán thành công")) {
            imgPaymentStatus.setImageResource(R.drawable.success);
        } else if (result.equals("Thanh toán đã được hủy")) {
            imgPaymentStatus.setImageResource(R.drawable.cancel);
        } else {
            imgPaymentStatus.setImageResource(R.drawable.error);
        }

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReturn = new Intent(PaymentNotificationActivity.this, MovieListActivity.class);
                startActivity(intentReturn);
                finish();
            }
        });
        
        btnViewTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentTickets = new Intent(PaymentNotificationActivity.this, TicketsActivity.class);
                startActivity(intentTickets);
                finish();
            }
        });
    }
}