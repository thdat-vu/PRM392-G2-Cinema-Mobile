package com.g2.moviebooking.ui.bookings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.g2.moviebooking.R;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.data.repository.ShowtimeRepository;
import com.g2.moviebooking.data.repository.TheatreRepository;
import com.g2.moviebooking.utils.QRCodeGenerator;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingHistoryInfoActivity extends AppCompatActivity {

    private static final String TAG = "BookingHistoryInfo";
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

    // Repositories
    private ShowtimeRepository showtimeRepository;
    private MovieRepository movieRepository;
    private TheatreRepository theatreRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history_info);
        Log.d(TAG, "onCreate: Khởi tạo BookingHistoryInfoActivity");

        // Khởi tạo repositories
        showtimeRepository = new ShowtimeRepository();
        movieRepository = new MovieRepository();
        theatreRepository = new TheatreRepository();
        Log.d(TAG, "onCreate: Đã khởi tạo các Repository");

        // Khởi tạo UI components
        initializeViews();
        Log.d(TAG, "onCreate: Đã khởi tạo views");

        // Lấy dữ liệu từ intent và fetch thông tin chi tiết
        getDataFromIntent();
    }

    private void initializeViews() {
        imgCinemaLogo = findViewById(R.id.imgCinemaLogo);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieFormat = findViewById(R.id.tvMovieFormat);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        tvBookingCode = findViewById(R.id.tvBookingCode);
        imgQrCode = findViewById(R.id.imgQrCode);
        tvShowtime = findViewById(R.id.tvShowtime);
        tvShowDate = findViewById(R.id.tvShowDate);
        tvTheaterNumber = findViewById(R.id.tvTheaterNumber);
        tvTicketCount = findViewById(R.id.tvTicketCount);
        tvSeatNumbers = findViewById(R.id.tvSeatNumbers);
        tvCinemaFullName = findViewById(R.id.tvCinemaFullName);
        tvCinemaAddress = findViewById(R.id.tvCinemaAddress);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvTransactionTime = findViewById(R.id.tvTransactionTime);
        btnBack = findViewById(R.id.btnBack);
        btnSupport = findViewById(R.id.btnSupport);
        btnClose = findViewById(R.id.btnClose);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        Log.d(TAG, "initializeViews: Đã tìm thấy các view");
    }

    private void getDataFromIntent() {
        booking = (Booking) getIntent().getSerializableExtra("booking");
        if (booking != null) {
            Log.d(TAG, "getDataFromIntent: Đã nhận booking ID = " + booking.getId());
            String showtimeId = booking.getShowtimeId();
            Log.d(TAG, "getDataFromIntent: showtimeId = " + showtimeId);

            if (showtimeId != null) {
                // Fetch Showtime
                showtimeRepository.getShowtimeDetail(showtimeId, new ShowtimeRepository.ShowtimeCallback<Showtime>() {
                    @Override
                    public void onSuccess(Showtime fetchedShowtime) {
                        showtime = fetchedShowtime;
                        Log.d(TAG, "getDataFromIntent: Đã nhận showtime ID = " + showtime.getId());
                        booking.setShowtime(showtime);

                        // Fetch Movie và Theatre
                        fetchMovieAndTheatre();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "getDataFromIntent: Lỗi khi fetch showtime - " + error);
                        Toast.makeText(BookingHistoryInfoActivity.this, "Failed to load showtime: " + error, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                Log.e(TAG, "getDataFromIntent: showtimeId is null");
                Toast.makeText(this, "Invalid booking data: showtimeId is null", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Log.e(TAG, "getDataFromIntent: Booking is null");
            Toast.makeText(this, "Invalid booking data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchMovieAndTheatre() {
        String movieId = showtime.getMovieId();
        String theatreId = showtime.getTheatreId();
        Log.d(TAG, "fetchMovieAndTheatre: Đang fetch movieId = " + movieId + " và theatreId = " + theatreId);

        // Fetch Movie
        movieRepository.getMovieDetail(movieId, new MovieRepository.MovieCallback<Movie>() {
            @Override
            public void onSuccess(Movie fetchedMovie) {
                movie = fetchedMovie;
                Log.d(TAG, "fetchMovieAndTheatre: Đã nhận movie title = " + movie.getTitle());
                showtime.setMovie(movie);

                // Fetch Theatre
                theatreRepository.getTheatreDetail(theatreId, new TheatreRepository.TheatreCallback<Theatre>() {
                    @Override
                    public void onSuccess(Theatre fetchedTheatre) {
                        theatre = fetchedTheatre;
                        Log.d(TAG, "fetchMovieAndTheatre: Đã nhận theatre name = " + theatre.getName());
                        showtime.setTheatre(theatre);

                        // Sau khi fetch xong, thiết lập UI
                        setupUI();
                        setupClickListeners();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e(TAG, "fetchMovieAndTheatre: Lỗi khi fetch theatre - " + error);
                        Toast.makeText(BookingHistoryInfoActivity.this, "Failed to load theatre: " + error, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "fetchMovieAndTheatre: Lỗi khi fetch movie - " + error);
                Toast.makeText(BookingHistoryInfoActivity.this, "Failed to load movie: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupUI() {
        if (booking == null || showtime == null || movie == null || theatre == null) {
            Log.e(TAG, "setupUI: Dữ liệu booking không hợp lệ");
            Toast.makeText(this, "Invalid booking data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Định dạng ngày giờ
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Set cinema info
        tvCinemaName.setText(theatre.getName());
        Log.d(TAG, "setupUI: Đã set tên rạp = " + theatre.getName());
        if (theatre.getLogoUrl() != null && !theatre.getLogoUrl().isEmpty()) {
            Glide.with(this).load(theatre.getLogoUrl()).into(imgCinemaLogo);
            Log.d(TAG, "setupUI: Đã load logo rạp từ " + theatre.getLogoUrl());
        } else {
            Log.w(TAG, "setupUI: Logo URL is null or empty");
        }

        // Set movie info
        tvMovieTitle.setText(movie.getTitle());
        Log.d(TAG, "setupUI: Đã set tiêu đề phim = " + movie.getTitle());
        tvMovieFormat.setText(showtime.getFormat() + " " + showtime.getLanguage());
        Log.d(TAG, "setupUI: Đã set định dạng phim = " + showtime.getFormat() + " " + showtime.getLanguage());
        if (movie.getBannerUrl() != null && !movie.getBannerUrl().isEmpty()) {
            Glide.with(this).load(movie.getBannerUrl()).into(imgMoviePoster);
            Log.d(TAG, "setupUI: Đã load poster phim từ " + movie.getBannerUrl());
        } else {
            Log.w(TAG, "setupUI: Banner URL is null or empty");
        }

        // Set booking info
        tvBookingCode.setText(booking.getBookingCode());
        Log.d(TAG, "setupUI: Đã set mã booking = " + booking.getBookingCode());
        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(booking.getBookingCode(), 500, 500);
        if (qrCodeBitmap != null) {
            imgQrCode.setImageBitmap(qrCodeBitmap);
            Log.d(TAG, "setupUI: Đã tạo và set QR code");
        } else {
            Log.w(TAG, "setupUI: Không tạo được QR code");
        }
        String startTimeStr = showtime.getStartTime() != null ? timeFormat.format(showtime.getStartTime()) : "N/A";
        String endTimeStr = showtime.getEndTime() != null ? timeFormat.format(showtime.getEndTime()) : "N/A";
        String dateStr = showtime.getDate() != null ? dateFormat.format(showtime.getDate()) : "N/A";
        tvShowtime.setText(startTimeStr + " - " + endTimeStr);
        tvShowDate.setText(dateStr);
        Log.d(TAG, "setupUI: Đã set thời gian chiếu = " + startTimeStr + " - " + endTimeStr + ", ngày = " + dateStr);

        // Set theater info
        tvTheaterNumber.setText(showtime.getScreenName());
        Log.d(TAG, "setupUI: Đã set số phòng chiếu = " + showtime.getScreenName());
        tvTicketCount.setText(String.valueOf(booking.getSeats().size()));
        Log.d(TAG, "setupUI: Đã set số vé = " + booking.getSeats().size());
        String seatNumbers = String.join(", ", booking.getSeats());
        tvSeatNumbers.setText(seatNumbers);
        Log.d(TAG, "setupUI: Đã set số ghế = " + seatNumbers);

        // Set cinema address
        tvCinemaFullName.setText(theatre.getName());
        tvCinemaAddress.setText(theatre.getAddress());
        Log.d(TAG, "setupUI: Đã set tên đầy đủ và địa chỉ rạp = " + theatre.getAddress());

        // Set transaction info
        tvTransactionId.setText(booking.getTransactionId() != null ? booking.getTransactionId() : "N/A");
        Log.d(TAG, "setupUI: Đã set transaction ID = " + (booking.getTransactionId() != null ? booking.getTransactionId() : "N/A"));
        if (booking.getTransactionTime() != null) {
            String transactionTimeStr = dateFormat.format(booking.getTransactionTime()) + " " +
                    timeFormat.format(booking.getTransactionTime());
            tvTransactionTime.setText(transactionTimeStr);
            Log.d(TAG, "setupUI: Đã set thời gian giao dịch = " + transactionTimeStr);
        } else {
            tvTransactionTime.setText("N/A");
            Log.d(TAG, "setupUI: Transaction time is null");
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            Log.d(TAG, "setupClickListeners: Đã click nút Back");
            onBackPressed();
        });
        btnClose.setOnClickListener(v -> {
            Log.d(TAG, "setupClickListeners: Đã click nút Close");
            finish();
        });
        btnSupport.setOnClickListener(v -> {
            Log.d(TAG, "setupClickListeners: Đã click nút Support");
            Toast.makeText(this, "Support feature not implemented yet", Toast.LENGTH_SHORT).show();
        });
        btnShare.setOnClickListener(v -> {
            Log.d(TAG, "setupClickListeners: Đã click nút Share");
            shareTicketInfo();
        });
        btnDownload.setOnClickListener(v -> {
            Log.d(TAG, "setupClickListeners: Đã click nút Download");
            downloadTicket();
        });
    }

    private void shareTicketInfo() {
        Log.d(TAG, "shareTicketInfo: Bắt đầu chia sẻ thông tin vé");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        StringBuilder shareText = new StringBuilder();
        shareText.append("Vé xem phim: ").append(tvMovieTitle.getText()).append("\n");
        shareText.append("Rạp: ").append(tvCinemaName.getText()).append("\n");
        shareText.append("Thời gian: ").append(tvShowtime.getText()).append(" ").append(tvShowDate.getText()).append("\n");
        shareText.append("Phòng chiếu: ").append(tvTheaterNumber.getText()).append("\n");
        shareText.append("Ghế: ").append(tvSeatNumbers.getText()).append("\n");
        shareText.append("Mã đặt vé: ").append(tvBookingCode.getText());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ vé xem phim"));
        Log.d(TAG, "shareTicketInfo: Đã mở Intent chia sẻ");
    }

    private void downloadTicket() {
        Log.d(TAG, "downloadTicket: Bắt đầu tải vé xuống");
        Toast.makeText(this, "Đang tải vé xuống...", Toast.LENGTH_SHORT).show();
        // TODO: Implement ticket download functionality
    }
}