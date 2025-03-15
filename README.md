# Movie Booking App

Ứng dụng Android đơn giản cho phép người dùng xem danh sách phim, chi tiết phim và đăng nhập bằng Google để truy cập. Ứng dụng sử dụng Firebase để quản lý dữ liệu và xác thực.

## Tính năng chính
- **Đăng nhập bằng Google**: Xác thực người dùng qua Firebase Authentication.
- **Danh sách phim**: Hiển thị danh sách phim từ Firestore trong RecyclerView dạng ngang với phân trang.
- **Chi tiết phim**: Xem thông tin chi tiết của phim bằng cách nhấn đúp vào mục trong danh sách.

## Công nghệ
- **Ngôn ngữ**: Java
- **Framework**: Android SDK
- **Database**: Firebase Firestore
- **Authentication**: Firebase Authentication (Google Sign-In)
- **Hình ảnh**: Glide (tải và hiển thị banner phim)

## Cài đặt
1. Clone repository:
   ```
   git clone <repository_url>
   ```
2. Thêm file `google-services.json` vào thư mục `app/` (tải từ Firebase Console).
3. Sync project với Gradle:
   ```
   ./gradlew build
   ```
4. Chạy ứng dụng trên emulator hoặc thiết bị thật.

## Cấu trúc thư mục
```
com.g2.moviebooking
├── data
│   ├── model
│   │   ├── Movie.java       # Model phim với các thuộc tính như tiêu đề, mô tả, thể loại...
│   │   └── User.java        # Model người dùng với tên và email
│   └── repository
│       ├── AuthRepository.java  # Xử lý đăng nhập Google và lưu user vào Firestore
│       └── MovieRepository.java # Truy vấn danh sách phim và chi tiết phim
├── ui
│   ├── auth
│   │   └── LoginActivity.java   # Màn hình đăng nhập Google
│   ├── MovieAdapter.java        # Adapter cho RecyclerView hiển thị danh sách phim
│   ├── MovieDetailActivity.java # Hiển thị chi tiết phim
│   └── MovieListActivity.java   # Danh sách phim với phân trang
└── utils
    └── FirebaseClient.java       # Singleton khởi tạo FirebaseAuth và Firestore
```

## Cách hoạt động
1. **LoginActivity**: 
   - Sử dụng Google Sign-In để xác thực.
   - Lưu thông tin người dùng (tên, email) vào Firestore sau khi đăng nhập thành công.
2. **MovieListActivity**: 
   - Tải danh sách phim từ Firestore với phân trang (10 phim/trang).
   - Sử dụng RecyclerView ngang với PagerSnapHelper để hiển thị.
   - Tự động tải thêm phim khi người dùng cuộn gần cuối danh sách.
3. **MovieDetailActivity**: 
   - Hiển thị chi tiết phim (tiêu đề, mô tả, thể loại, đạo diễn, diễn viên...) khi nhấn đúp vào phim.
   - Sử dụng Glide để tải banner phim.

## TODO
- [ ] Thêm tính năng đặt vé (chọn rạp, giờ chiếu, ghế ngồi).
- [ ] Tích hợp cổng thanh toán (ví dụ: Stripe, PayPal).
- [ ] Thêm bộ lọc phim theo thể loại, đánh giá hoặc ngày phát hành.
- [ ] Cải thiện giao diện (thêm animation khi chuyển màn hình, hỗ trợ dark mode).
- [ ] Thêm xử lý lỗi mạng và lưu trữ offline bằng Room hoặc SharedPreferences.

## Góp ý
- Hiện tại ứng dụng chỉ hỗ trợ xem danh sách và chi tiết phim. Để biến nó thành ứng dụng đặt vé hoàn chỉnh, cần thêm logic đặt vé và giao diện tương ứng.
- Có thể tích hợp API bên thứ ba như The Movie Database (TMDb) để làm phong phú dữ liệu phim.
