# Ứng Dụng Đặt Vé Xem Phim

Ứng dụng Android cho phép người dùng duyệt phim, xem chi tiết và đặt vé với giao diện mượt mà và thông tin phim đầy đủ.

## Tính Năng

- **Xác Thực Người Dùng**: Đăng nhập (Email/Mật khẩu, Google), Đăng ký, Xác thực bằng JWT.  
- **Duyệt Phim**: Danh sách phim cuộn ngang với hiệu ứng thu phóng và phân trang.  
- **Chi Tiết Phim**: Thông tin phim (tiêu đề, mô tả, thời lượng, ngày phát hành, đạo diễn, diễn viên, thể loại, đánh giá) kèm ảnh banner.  
- **Điều Hướng**: Thanh điều hướng dưới cùng, giao diện trực quan.

## Cấu Trúc Dự Án

```
App
├── UI Layer
│   ├── Authentication
│   │   ├── LoginActivity → AuthRepository → RetrofitClient
│   │   └── RegistrationActivity → AuthRepository → RetrofitClient
│   └── Movie Browsing
│       ├── MovieListActivity → MovieRepository → RetrofitClient
│       ├── MovieAdapter
│       └── MovieDetailActivity → MovieRepository → RetrofitClient
├── Data Layer
│   ├── Remote
│   │   ├── ApiService → RetrofitClient
│   │   ├── Entity Models
│   │   ├── Request Models
│   │   └── Response Models
│   └── Repository
│       ├── AuthRepository → RetrofitClient
│       └── MovieRepository → RetrofitClient
└── Utils
    └── RetrofitClient
```

## Kiến Trúc

Sử dụng mô hình Repository đơn giản hóa:  
- **UI Layer**: Hiển thị giao diện và xử lý tương tác.  
- **Repository Layer**: Kết nối dữ liệu và UI.  
- **Remote Layer**: Giao tiếp API qua Retrofit.

## Thư Viện

- Retrofit (Yêu cầu HTTP)  
- Gson (Xử lý JSON)  
- Glide (Tải ảnh)  
- Firebase Authentication (Đăng nhập Google)  
- RecyclerView (Hiển thị danh sách)  
- Material Design Components (Giao diện)

## Cài Đặt

### Yêu Cầu
- Android Studio Arctic Fox (2020.3.1) trở lên  
- JDK 11+  
- Target SDK: 30+  
- Minimum SDK: 21 (Android 5.0)

### Cấu Hình Firebase
1. Tạo dự án tại [Firebase Console](https://console.firebase.google.com/).  
2. Thêm ứng dụng Android, tải `google-services.json` vào thư mục `app`.  
3. Bật Google Sign-In trong Firebase Authentication.

### Cấu Hình API
- Sử dụng API tại `https://prm-392-g2-cinema.vercel.app/`.  
- Đảm bảo API hoạt động.

### Hướng Dẫn
1. Clone repository:
   ```bash
   git clone https://github.com/<tên-người-dùng>/movie-booking-app.git
   ```
2. Mở bằng Android Studio.  
3. Đồng bộ thư viện.  
4. Build và chạy.

## API Endpoints

### Xác Thực
- `POST /auth/google`: Xác thực token Google  
- `POST /api/auth/login`: Đăng nhập bằng email/mật khẩu  
- `POST /api/users`: Đăng ký người dùng  

### Phim
- `GET /api/movies`: Danh sách phim (phân trang)  
- `GET /api/movies/{id}`: Chi tiết phim  

## Công Việc Cần Làm
- [x] Màn hình đăng nhập/đăng ký  
- [x] Đăng nhập Google  
- [x] Danh sách phim với phân trang  
- [x] Hiệu ứng UI  
- [x] Chi tiết phim  
- [ ] Chọn rạp chiếu  
- [ ] Chọn lịch chiếu  
- [ ] Chọn ghế  
- [ ] Thanh toán vé  
- [ ] Quản lý vé  

## Đóng Góp

1. Fork repository.  
2. Tạo nhánh: `git checkout -b feature/tính-năng-mới`.  
3. Commit: `git commit -m 'Thêm tính năng mới'`.  
4. Push: `git push origin feature/tính-năng-mới`.  
5. Tạo Pull Request.

## Giấy Phép

MIT License - xem [LICENSE](LICENSE) để biết thêm.
