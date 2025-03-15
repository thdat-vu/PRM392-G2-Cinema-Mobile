package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.g2.moviebooking.R;
import com.g2.moviebooking.utils.QRCodeGenerator;

public class BookingHistoryInfoActivity extends AppCompatActivity {

    // UI Components
    private ImageView imgCinemaLogo, imgMoviePoster, imgQrCode;
    private TextView tvCinemaName, tvMovieTitle, tvMovieFormat;
    private TextView tvBookingCode, tvShowtime, tvShowDate;
    private TextView tvTheaterNumber, tvTicketCount, tvSeatNumbers;
    private TextView tvCinemaFullName, tvCinemaAddress;
    private TextView tvTransactionId, tvTransactionTime;
    private ImageButton btnBack, btnSupport, btnClose, btnShare, btnDownload;

    // Data
    private Booking booking;
    private Movie movie;
    private Showtime showtime;
    private Theatre theatre;
    private String bookingCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history_info);

        // Initialize UI components
        initializeViews();

        // Get data from intent
        getDataFromIntent();

        // Set up UI with data
        setupUI();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Cinema and Movie Info
        imgCinemaLogo = findViewById(R.id.imgCinemaLogo);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieFormat = findViewById(R.id.tvMovieFormat);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);

        // Booking Info
        tvBookingCode = findViewById(R.id.tvBookingCode);
        imgQrCode = findViewById(R.id.imgQrCode);
        tvShowtime = findViewById(R.id.tvShowtime);
        tvShowDate = findViewById(R.id.tvShowDate);

        // Theater Info
        tvTheaterNumber = findViewById(R.id.tvTheaterNumber);
        tvTicketCount = findViewById(R.id.tvTicketCount);
        tvSeatNumbers = findViewById(R.id.tvSeatNumbers);

        // Cinema Address
        tvCinemaFullName = findViewById(R.id.tvCinemaFullName);
        tvCinemaAddress = findViewById(R.id.tvCinemaAddress);

        // Transaction Info
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvTransactionTime = findViewById(R.id.tvTransactionTime);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnSupport = findViewById(R.id.btnSupport);
        btnClose = findViewById(R.id.btnClose);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
    }

    private void getDataFromIntent() {
        // In a real app, you would get the booking data from the intent
        // For this example, we'll use dummy data

        // TODO: Replace with actual data from intent
        // booking = (Booking) getIntent().getSerializableExtra("booking");
        // movie = booking.getMovie();
        // showtime = booking.getShowtime();
        // theatre = booking.getTheatre();

        // For now, we'll use the values from the screenshot
        bookingCode = "WNZS8ZS";
    }

    private void setupUI() {
        // Set cinema info
        // In a real app, you would load the cinema logo using Glide
        // Glide.with(this).load(theatre.getLogoUrl()).into(imgCinemaLogo);

        tvCinemaName.setText("Galaxy Linh Trung");
        tvMovieTitle.setText("Nhà Giả Tiền");
        tvMovieFormat.setText("2D Phụ đề");

        // In a real app, you would load the movie poster using Glide
        // Glide.with(this).load(movie.getPosterUrl()).into(imgMoviePoster);

        // Set booking info
        tvBookingCode.setText(bookingCode);

        // Generate and set QR code
        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(bookingCode, 500, 500);
        if (qrCodeBitmap != null) {
            imgQrCode.setImageBitmap(qrCodeBitmap);
        }

        tvShowtime.setText("13:50 - 15:47");
        tvShowDate.setText("CN, 23/02/2025");

        // Set theater info
        tvTheaterNumber.setText("RẠP 2");
        tvTicketCount.setText("03");
        tvSeatNumbers.setText("E07, E08, E09");

        // Set cinema address
        tvCinemaFullName.setText("Galaxy Linh Trung");
        tvCinemaAddress.setText("Tầng trệt | TTTM Co.opXtra Linh Trung, số 934 Quốc Lộ 1A, P. Linh Trung, Q. Thủ Đức, TP.HCM");

        // Set transaction info
        tvTransactionId.setText("79781002660");
        tvTransactionTime.setText("00:34 - 23/02/2025");
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnClose.setOnClickListener(v -> finish());

        btnSupport.setOnClickListener(v -> {
            // Open support dialog or activity
            Toast.makeText(this, "Support feature not implemented yet", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            // Share ticket info
            shareTicketInfo();
        });

        btnDownload.setOnClickListener(v -> {
            // Download ticket as PDF or image
            downloadTicket();
        });
    }

    private void shareTicketInfo() {
        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // Build share text
        StringBuilder shareText = new StringBuilder();
        shareText.append("Vé xem phim: ").append(tvMovieTitle.getText()).append("\n");
        shareText.append("Rạp: ").append(tvCinemaName.getText()).append("\n");
        shareText.append("Thời gian: ").append(tvShowtime.getText()).append(" ").append(tvShowDate.getText()).append("\n");
        shareText.append("Phòng chiếu: ").append(tvTheaterNumber.getText()).append("\n");
        shareText.append("Ghế: ").append(tvSeatNumbers.getText()).append("\n");
        shareText.append("Mã đặt vé: ").append(tvBookingCode.getText());

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ vé xem phim"));
    }

    private void downloadTicket() {
        // In a real app, you would generate a PDF or image of the ticket
        // For now, just show a toast
        Toast.makeText(this, "Đang tải vé xuống...", Toast.LENGTH_SHORT).show();

        // TODO: Implement ticket download functionality
    }
}