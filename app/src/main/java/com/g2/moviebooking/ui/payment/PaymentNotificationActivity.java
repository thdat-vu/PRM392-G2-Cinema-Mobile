package com.g2.moviebooking.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.g2.moviebooking.R;
import com.g2.moviebooking.ui.MovieListActivity;

public class PaymentNotificationActivity extends AppCompatActivity {
    TextView tvNotify;
    TextView tvTotal;
    Button btnReturn;
    ImageView imgPaymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        tvNotify = findViewById(R.id.tvNotify);
        tvTotal= findViewById(R.id.tvTotal);
        btnReturn = findViewById(R.id.btnReturn);
        imgPaymentStatus = findViewById(R.id.imgPaymentStatus);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        tvNotify.setText(result);
        tvTotal.setText(intent.getStringExtra("total"));

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
            }
        });
    }
}