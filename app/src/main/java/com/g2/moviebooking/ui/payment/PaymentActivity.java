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
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.ui.payment.Api.CreateOrder;
import com.g2.moviebooking.ui.payment.Constant.AppInfo;
import com.g2.moviebooking.utils.Constants;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    TextView tvAmount, selectedSeatsText;
    Button btnCheckout;
    
    // Booking information
    private double totalAmount;
    private String[] selectedSeats;
    private String movieTitle;
    private String theatreName;
    private String showtimeText;
    private String bookingCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_details);

        tvAmount = findViewById(R.id.tvAmount);
        selectedSeatsText = findViewById(R.id.tvSelectedSeatsText);
        btnCheckout = findViewById(R.id.btnCheckout);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        // Get booking information from intent
        getBookingInfoFromIntent();
        
        // Display total amount
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalString = String.format("%.0f", totalAmount);
        String totalFormatted = formatter.format(totalAmount);
        tvAmount.setText(totalFormatted);
        
        // Display selected seats
        if (selectedSeats != null && selectedSeats.length > 0) {
            String seatsText = String.join(", ", selectedSeats);
            selectedSeatsText.setText(seatsText);
        } else {
            selectedSeatsText.setText("Không có ghế nào được chọn");
        }

        btnCheckout.setOnClickListener(v -> {
            CreateOrder orderApi = new CreateOrder();
            try {
                JSONObject data = orderApi.createOrder(totalString);
                String code = data.getString("return_code");

                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    String transactionId = data.getString("order_id");
                    
                    // Make sure this URL matches your intent filter scheme and host
                    ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String s, String s1, String s2) {
                            // Create booking object with payment information
                            Booking booking = createBookingObject(transactionId, "Completed");
                            
                            // Pass booking information to success screen
                            Intent intent1 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent1.putExtra("result", "Thanh toán thành công");
                            intent1.putExtra("total", "Bạn đã thanh toán " + totalFormatted);
                            intent1.putExtra("booking", booking);
                            intent1.putExtra("bookingCode", bookingCode);
                            startActivity(intent1);
                        }

                        @Override
                        public void onPaymentCanceled(String s, String s1) {
                            // Create booking object with canceled status
                            Booking booking = createBookingObject(transactionId, "Canceled");
                            
                            Intent intent2 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent2.putExtra("result", "Thanh toán đã được hủy");
                            intent2.putExtra("booking", booking);
                            startActivity(intent2);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                            Log.e("ZaloPay Error", "Error: " + zaloPayError.toString() + " | " + s + " | " + s1);
                            
                            // Create booking object with error status
                            Booking booking = createBookingObject(transactionId, "Failed");
                            
                            Intent intent3 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent3.putExtra("result", "Lỗi thanh toán: " + zaloPayError.toString());
                            intent3.putExtra("booking", booking);
                            startActivity(intent3);
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void getBookingInfoFromIntent() {
        Intent intent = getIntent();
        
        // Get total amount
        totalAmount = intent.getDoubleExtra(Constants.EXTRA_TOTAL_AMOUNT, 0);
        
        // Get selected seats
        selectedSeats = intent.getStringArrayExtra(Constants.EXTRA_SELECTED_SEATS);
        
        // Get movie information
        movieTitle = intent.getStringExtra(Constants.EXTRA_MOVIE_TITLE);
        theatreName = intent.getStringExtra(Constants.EXTRA_THEATRE_NAME);
        showtimeText = intent.getStringExtra(Constants.EXTRA_SHOWTIME);
        
        // Generate a unique booking code
        bookingCode = generateBookingCode();
    }
    
    private String generateBookingCode() {
        // Generate a simple 6-character alphanumeric booking code
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private Booking createBookingObject(String transactionId, String paymentStatus) {
        Booking booking = new Booking();
        
        // Set basic booking information
        booking.setBookingCode(bookingCode);
        booking.setTotalAmount(totalAmount);
        booking.setBookingDate(new Date());
        
        // Set payment information
        booking.setPaymentMethod("ZaloPay");
        booking.setPaymentStatus(paymentStatus);
        booking.setTransactionId(transactionId);
        booking.setTransactionTime(new Date());
        
        // Set seats
        if (selectedSeats != null) {
            booking.setSeats(Arrays.asList(selectedSeats));
        }
        
        // Set status based on payment status
        if ("Completed".equals(paymentStatus)) {
            booking.setStatus("Confirmed");
        } else if ("Canceled".equals(paymentStatus)) {
            booking.setStatus("Canceled");
        } else {
            booking.setStatus("Failed");
        }
        
        return booking;
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