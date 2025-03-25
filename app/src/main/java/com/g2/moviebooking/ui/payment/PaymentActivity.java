package com.g2.moviebooking.ui.payment;

import static android.view.View.INVISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.g2.moviebooking.R;
import com.g2.moviebooking.adapter.ComboAdapter;
import com.g2.moviebooking.data.model.Booking;
import com.g2.moviebooking.data.model.Movie;
import com.g2.moviebooking.data.model.Showtime;
import com.g2.moviebooking.data.model.Theatre;
import com.g2.moviebooking.data.repository.BookingRepository;
import com.g2.moviebooking.data.repository.MovieRepository;
import com.g2.moviebooking.data.repository.ShowtimeRepository;
import com.g2.moviebooking.data.repository.TheatreRepository;
import com.g2.moviebooking.model.FoodDrink;
import com.g2.moviebooking.ui.payment.Api.CreateOrder;
import com.g2.moviebooking.ui.payment.Constant.AppInfo;
import com.g2.moviebooking.ui.auth.LoginActivity;
import com.g2.moviebooking.utils.Constants;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    TextView tvAmount, selectedSeatsText, tvCinemaName, tvFilmName, tvComboName,
            tvShowtime, tvFormat, tvScreen, tvMovieDescription;
    ImageView imgFilm;
    Button btnCheckout;
    CardView tvComboSection;

    // Booking information
    private double totalAmount;
    private double seatPrice;
    private double foodPrice;
    private String[] selectedSeats;
    private Showtime showtime;
    private String bookingCode;
    private RecyclerView rvCombos;
    private List<FoodDrink> foodItems;
    private TheatreRepository theatreRepository;
    private MovieRepository movieRepository;
    private BookingRepository bookingRepository;
    private ShowtimeRepository showtimeRepository;
    private FirebaseAuth auth;
    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_details);
        Log.d(TAG, "onCreate: Activity started");

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Khởi tạo repositories
        theatreRepository = new TheatreRepository();
        movieRepository = new MovieRepository();
        bookingRepository = new BookingRepository();
        showtimeRepository = new ShowtimeRepository();

        // Get booking information from intent
        getBookingInfoFromIntent();
        Log.d(TAG, "onCreate: Retrieved booking info - totalAmount: " + totalAmount);

        // Khởi tạo các view
        rvCombos = findViewById(R.id.rvCombos);
        tvAmount = findViewById(R.id.tvAmount);
        selectedSeatsText = findViewById(R.id.tvSelectedSeatsText);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvFilmName = findViewById(R.id.tvFilmName);
        tvShowtime = findViewById(R.id.tvShowtime);
        tvFormat = findViewById(R.id.tvFormat);
        tvScreen = findViewById(R.id.tvScreen);
        tvMovieDescription = findViewById(R.id.tvMovieDescription);
        tvComboName = findViewById(R.id.tvComboName);
        imgFilm = findViewById(R.id.imgFilm);
        tvComboSection = findViewById(R.id.tvComboSection);

        // Cấu hình StrictMode (nếu cần cho ZaloPay)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        // Vô hiệu hóa nút Checkout cho đến khi dữ liệu Movie và Theatre được tải
        btnCheckout.setEnabled(false);

        // Định dạng tổng số tiền
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String totalFormatted = formatter.format(totalAmount);

        // Hiển thị UI với dữ liệu ban đầu
        displayUI(totalFormatted);
    }

    private void payWithZaloPay(String totalFormatted) {
        // Kiểm tra người dùng đã đăng nhập chưa
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục thanh toán", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Kiểm tra showtimeId và selectedSeats
        if (showtime == null || showtime.getId() == null) {
            Toast.makeText(this, "Không tìm thấy thông tin suất chiếu", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (selectedSeats == null || selectedSeats.length == 0) {
            Toast.makeText(this, "Không có ghế nào được chọn", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        CreateOrder orderApi = new CreateOrder();
        try {
            String totalString = String.format("%.0f", totalAmount);
            JSONObject data = orderApi.createOrder(totalString);
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");

                ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                        // Tạo booking object với trạng thái thanh toán thành công
                        Booking booking = createBookingObject("PAID", "CONFIRMED", transactionId);

                        // Bước 1: Cập nhật ghế trong Showtime
                        showtimeRepository.updateSeats(
                                showtime.getId(),
                                Arrays.asList(selectedSeats),
                                new ShowtimeRepository.ShowtimeCallback<Showtime>() {
                                    @Override
                                    public void onSuccess(Showtime updatedShowtime) {
                                        Log.d(TAG, "Seats updated successfully for showtime: " + showtime.getId());

                                        // Bước 2: Upload booking lên Firestore
                                        uploadBookingToFirestore(booking, totalFormatted);
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Log.e(TAG, "Failed to update seats: " + error);
                                        Toast.makeText(PaymentActivity.this, "Lỗi khi cập nhật ghế: " + error, Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        // Tạo booking object với trạng thái hủy
                        Booking booking = createBookingObject("CANCELED", "CANCELED", appTransID);

                        // Upload booking lên Firestore
                        uploadBookingToFirestore(booking, totalFormatted);
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        Log.e(TAG, "ZaloPay Error: " + zaloPayError.toString() + " | " + zpTransToken + " | " + appTransID);
                        // Tạo booking object với trạng thái lỗi
                        Booking booking = createBookingObject("FAILED", "FAILED", appTransID);

                        // Upload booking lên Firestore
                        uploadBookingToFirestore(booking, totalFormatted);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tạo đơn hàng ZaloPay", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadBookingToFirestore(Booking booking, String totalFormatted) {
        bookingRepository.addBooking(booking, new BookingRepository.BookingCallback<String>() {
            @Override
            public void onSuccess(String bookingId) {
                Log.d(TAG, "Booking created successfully with ID: " + bookingId);
                booking.setId(bookingId); // Gán ID từ Firestore vào booking

                // Chuyển sang PaymentNotificationActivity
                Intent intent = new Intent(PaymentActivity.this, PaymentNotificationActivity.class);
                intent.putExtra("result", booking.getStatus().equals("CONFIRMED") ? "Thanh toán thành công" :
                        (booking.getStatus().equals("CANCELED") ? "Thanh toán đã được hủy" : "Lỗi thanh toán"));
                intent.putExtra("total", "Bạn đã thanh toán " + totalFormatted);
                intent.putExtra("booking", booking);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to create booking: " + error);
                Toast.makeText(PaymentActivity.this, "Lỗi khi lưu booking: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getBookingInfoFromIntent() {
        Intent intent = getIntent();

        // Get total amount
        seatPrice = intent.getDoubleExtra(Constants.EXTRA_SEAT_PRICE, 0);
        foodPrice = intent.getDoubleExtra(Constants.EXTRA_FOOD_DRINKS_PRICE, 0);
        totalAmount = seatPrice + foodPrice;

        // Get list food items
        Serializable extra = intent.getSerializableExtra(Constants.EXTRA_FOOD_DRINKS_ITEMS);
        if (extra != null) {
            foodItems = (ArrayList<FoodDrink>) extra;
        } else {
            Log.e(TAG, "No food items were passed in the intent.");
        }

        // Get selected seats
        selectedSeats = intent.getStringArrayExtra(Constants.EXTRA_SELECTED_SEATS);

        // Get showtime information
        showtime = (Showtime) intent.getSerializableExtra(Constants.EXTRA_SHOWTIME);

        // Generate a unique booking code
        bookingCode = generateBookingCode();
    }

    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis();
    }

    private Booking createBookingObject(String paymentStatus, String status, String transactionId) {
        Booking booking = new Booking();

        // Lấy userId từ FirebaseAuth
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "unknown_user"; // Fallback nếu không có user

        // Gán các trường theo entity Booking
        booking.setUserId(userId);
        booking.setShowtimeId(showtime.getId());
        // Không lưu toàn bộ Showtime object
        booking.setSeats(Arrays.asList(selectedSeats));
        booking.setTotalAmount(totalAmount);
        booking.setBookingCode(bookingCode);
        booking.setBookingDate(new Date());
        booking.setPaymentMethod("ZaloPay");
        booking.setPaymentStatus(paymentStatus);
        booking.setTransactionId(transactionId);
        booking.setTransactionTime(new Date());
        booking.setStatus(status);
        booking.setFoodItems(convertFoodItems(foodItems));

        return booking;
    }

    // Chuyển đổi List<FoodDrink> thành List<Booking.FoodItem>, chỉ lưu id và quantity
    private List<Booking.FoodItem> convertFoodItems(List<FoodDrink> foodDrinks) {
        if (foodDrinks == null || foodDrinks.isEmpty()) return null;
        List<Booking.FoodItem> foodItems = new ArrayList<>();
        for (FoodDrink foodDrink : foodDrinks) {
            if (foodDrink.getQuantity() > 0) { // Chỉ thêm các item có quantity > 0
                Booking.FoodItem foodItem = new Booking.FoodItem();
                foodItem.setId(foodDrink.getId());
                foodItem.setQuantity(foodDrink.getQuantity());
                // Không lưu name và price
                foodItems.add(foodItem);
            }
        }
        return foodItems.isEmpty() ? null : foodItems;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayUI(String totalFormatted) {
        if (showtime == null) {
            Log.e(TAG, "Showtime is null");
            Toast.makeText(this, "Không tìm thấy thông tin suất chiếu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin cơ bản trước
        tvAmount.setText(totalFormatted);
        tvShowtime.setText(showtime.getFormattedShowtime());
        tvFormat.setText(showtime.getFormat());
        tvScreen.setText(showtime.getScreenName());

        // Hiển thị ghế đã chọn
        if (selectedSeats != null && selectedSeats.length > 0) {
            String seatsText = String.join(", ", selectedSeats);
            selectedSeatsText.setText(seatsText);
        } else {
            selectedSeatsText.setText("Không có ghế nào được chọn");
        }

        // Hiển thị danh sách combo đồ ăn
        if (foodItems != null && !foodItems.isEmpty()) {
            ComboAdapter comboAdapter = new ComboAdapter(foodItems);
            rvCombos.setLayoutManager(new LinearLayoutManager(this));
            rvCombos.setAdapter(comboAdapter);
            Log.d(TAG, "RecyclerView adapter set with " + foodItems.size() + " items");
        } else {
            tvComboName.setVisibility(INVISIBLE);
            tvComboSection.setVisibility(INVISIBLE);
            Log.d(TAG, "No food items to display");
        }

        // Lấy thông tin Movie và Theatre từ Firestore
        String movieId = showtime.getMovieId();
        String theatreId = showtime.getTheatreId();

        if (movieId == null || movieId.isEmpty() || theatreId == null || theatreId.isEmpty()) {
            Log.e(TAG, "MovieId or TheatreId is null/empty - MovieId: " + movieId + ", TheatreId: " + theatreId);
            Toast.makeText(this, "Dữ liệu phim hoặc rạp không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Biến để theo dõi khi cả Movie và Theatre đều được tải
        final boolean[] isMovieLoaded = {false};
        final boolean[] isTheatreLoaded = {false};

        // Lấy Movie từ Firestore
        movieRepository.getMovieDetail(movieId, new MovieRepository.MovieCallback<Movie>() {
            @Override
            public void onSuccess(Movie movie) {
                if (movie != null) {
                    tvFilmName.setText(movie.getTitle());
                    tvMovieDescription.setText(movie.getDescription());
                    Glide.with(PaymentActivity.this)
                            .load(movie.getBannerUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgFilm);
                    showtime.setMovie(movie); // Cập nhật Movie vào Showtime
                    Log.d(TAG, "Movie loaded: " + movie.getTitle());
                } else {
                    tvFilmName.setText("Phim không xác định");
                    tvMovieDescription.setText("");
                    Log.w(TAG, "Movie is null from repository");
                }
                isMovieLoaded[0] = true;
                checkDataLoaded(isMovieLoaded[0], isTheatreLoaded[0], totalFormatted);
            }

            @Override
            public void onFailure(String error) {
                tvFilmName.setText("Lỗi tải thông tin phim");
                tvMovieDescription.setText("");
                Log.e(TAG, "Failed to load movie: " + error);
                isMovieLoaded[0] = true;
                checkDataLoaded(isMovieLoaded[0], isTheatreLoaded[0], totalFormatted);
            }
        });

        // Lấy Theatre từ Firestore
        theatreRepository.getTheatreDetail(theatreId, new TheatreRepository.TheatreCallback<Theatre>() {
            @Override
            public void onSuccess(Theatre theatre) {
                if (theatre != null) {
                    tvCinemaName.setText(theatre.getName());
                    showtime.setTheatre(theatre); // Cập nhật Theatre vào Showtime
                    Log.d(TAG, "Theatre loaded: " + theatre.getName());
                } else {
                    tvCinemaName.setText("Rạp không xác định");
                    Log.w(TAG, "Theatre is null from repository");
                }
                isTheatreLoaded[0] = true;
                checkDataLoaded(isMovieLoaded[0], isTheatreLoaded[0], totalFormatted);
            }

            @Override
            public void onFailure(String error) {
                tvCinemaName.setText("Lỗi tải thông tin rạp");
                Log.e(TAG, "Failed to load theatre: " + error);
                isTheatreLoaded[0] = true;
                checkDataLoaded(isMovieLoaded[0], isTheatreLoaded[0], totalFormatted);
            }
        });
    }

    // Kiểm tra khi cả Movie và Theatre đều được tải để bật nút Checkout
    private void checkDataLoaded(boolean isMovieLoaded, boolean isTheatreLoaded, String totalFormatted) {
        if (isMovieLoaded && isTheatreLoaded) {
            btnCheckout.setEnabled(true);
            btnCheckout.setOnClickListener(v -> payWithZaloPay(totalFormatted));
            Log.d(TAG, "All data loaded, Checkout button enabled");
        }
    }
}