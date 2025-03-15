package com.g2.moviebooking.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.moviebooking.R;
import com.g2.moviebooking.ui.payment.Api.CreateOrder;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    TextView tvAmount, tvTotal;
    Button btnCheckout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        tvAmount = findViewById(R.id.tvAmount);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        Intent intent = getIntent();
        tvAmount.setText(intent.getStringExtra("soluong"));
        Double total = intent.getDoubleExtra("total", 0);

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalString = String.format("%.0f", total);
        String totalFormatted = formatter.format(total);
        tvTotal.setText(totalFormatted);

        btnCheckout.setOnClickListener(v -> {
            CreateOrder orderApi = new CreateOrder();
            try {
                JSONObject data = orderApi.createOrder(totalString);
                String code = data.getString("return_code");

                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    // Make sure this URL matches your intent filter scheme and host
                    ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String s, String s1, String s2) {
                            Intent intent1 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent1.putExtra("result", "Thanh toán thành công");
                            intent1.putExtra("total", "Bạn đã thanh toán " + totalFormatted);
                            startActivity(intent1);
                        }

                        @Override
                        public void onPaymentCanceled(String s, String s1) {
                            Intent intent2 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent2.putExtra("result",  "Thanh toán đã được hủy");
                            startActivity(intent2);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                            Log.e("ZaloPay Error", "Error: " + zaloPayError.toString() + " | " + s + " | " + s1);
                            Intent intent3 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent3.putExtra("result", "Lỗi thanh toán: " + zaloPayError.toString());
                            startActivity(intent3);
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    // Add this method to handle activity results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  ZaloPaySDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}