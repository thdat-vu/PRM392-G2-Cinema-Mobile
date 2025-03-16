package com.g2.moviebooking.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;
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
    TextView tvAmount, selectedSeatsText, tvCinemaName, tvFilmName,
            tvShowtime, tvFormat, tvScreen;
    ImageView imgFilm;
    Button btnCheckout;
    
    // Booking information
    private double totalAmount;
    private String[] selectedSeats;
    private Showtime showtime;
    private String bookingCode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_details);

        tvAmount = findViewById(R.id.tvAmount);
        selectedSeatsText = findViewById(R.id.tvSelectedSeatsText);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvFilmName = findViewById(R.id.tvFilmName);
        tvShowtime = findViewById(R.id.tvShowtime);
        tvFormat = findViewById(R.id.tvFormat);
        tvScreen = findViewById(R.id.tvScreen);
        imgFilm = findViewById(R.id.imgFilm);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        // Get booking information from intent
        getBookingInfoFromIntent();
        
        // Display total amount
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalFormatted = formatter.format(totalAmount);
        displayUI(totalFormatted);

        btnCheckout.setOnClickListener(v -> {
            CreateOrder orderApi = new CreateOrder();
            try {
                String totalString = String.format("%.0f", totalAmount);
                JSONObject data = orderApi.createOrder(totalString);
                String code = data.getString("return_code");

                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    
                    // Make sure this URL matches your intent filter scheme and host
                    ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                            // Create booking object with payment information
                            Booking booking = createBookingObject(appTransID, "Completed");
                            
                            // Pass booking information to success screen
                            Intent intent1 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent1.putExtra("result", "Thanh toán thành công");
                            intent1.putExtra("total", "Bạn đã thanh toán " + totalFormatted);
                            intent1.putExtra("booking", booking);
                            intent1.putExtra("bookingCode", bookingCode);
                            startActivity(intent1);
                        }

                        @Override
                        public void onPaymentCanceled(String zpTransToken, String appTransID) {
                            // Create booking object with canceled status
                            Booking booking = createBookingObject(appTransID, "Canceled");
                            
                            Intent intent2 = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                            intent2.putExtra("result", "Thanh toán đã được hủy");
                            intent2.putExtra("booking", booking);
                            startActivity(intent2);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                            Log.e("ZaloPay Error", "Error: " + zaloPayError.toString() + " | " + zpTransToken + " | " + appTransID);
                            
                            // Create booking object with error status
                            Booking booking = createBookingObject(appTransID, "Failed");
                            
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

        // Get showtime information
        showtime = (Showtime) intent.getSerializableExtra(Constants.EXTRA_SHOWTIME);
        
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

        // Set showtime
        booking.setShowtime(showtime);
        
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

    private void displayUI(String totalFormatted){
        Movie movie = showtime.getMovie();
        Theatre theatre = showtime.getTheatre();

        tvAmount.setText(totalFormatted);
        tvCinemaName.setText(theatre.getName());
        tvFilmName.setText(movie.getTitle());
        tvShowtime.setText(showtime.getFormattedShowtime());
        tvFormat.setText(showtime.getFormat());
        tvScreen.setText(showtime.getScreenName());
        Glide.with(this)
                .load(movie.getBannerUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgFilm);

        // Display selected seats
        if (selectedSeats != null && selectedSeats.length > 0) {
            String seatsText = String.join(", ", selectedSeats);
            selectedSeatsText.setText(seatsText);
        } else {
            selectedSeatsText.setText("Không có ghế nào được chọn");
        }
    }
}