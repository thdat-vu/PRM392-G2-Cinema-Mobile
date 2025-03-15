# Ứng Dụng Đặt Vé Xem Phim

## Tổng Quan

Ứng dụng đặt vé xem phim là một ứng dụng Android cho phép người dùng duyệt phim, xem thông tin chi tiết và đặt vé. Ứng dụng cung cấp trải nghiệm người dùng mượt mà với giao diện đẹp mắt và thông tin phim toàn diện.

## Tính Năng

- **Xác Thực Người Dùng**
  - Đăng nhập bằng Email & Mật khẩu
  - Tích hợp đăng nhập Google
  - Đăng ký người dùng
  - Xác thực bằng JWT token

- **Duyệt Phim**
  - Danh sách phim cuộn ngang với hiệu ứng thu phóng
  - Hỗ trợ phân trang để tải dữ liệu hiệu quả
  - Giao diện đẹp mắt với hình ảnh phim và thông tin cơ bản

- **Chi Tiết Phim**
  - Thông tin phim đầy đủ bao gồm:
    - Tiêu đề, mô tả, thời lượng
    - Ngày phát hành, đạo diễn, diễn viên
    - Thể loại và đánh giá
  - Hiển thị ảnh banner

- **Điều Hướng**
  - Navigation bar phía dưới để dễ dàng truy cập các phần khác nhau
  - Giao diện người dùng trực quan

## Cấu Trúc Dự Án

### Data Layer

#### Remote
- **API Service**: Interface định nghĩa tất cả các endpoint API
- **Entity Models**: Các class dữ liệu biểu diễn các entity cốt lõi như Movie
- **Request Models**: Các class dữ liệu cho API request
- **Response Models**: Các class dữ liệu cho API response

#### Repository
- **AuthRepository**: Xử lý các API call liên quan đến xác thực
- **MovieRepository**: Quản lý các thao tác dữ liệu liên quan đến phim

### UI Layer

- **Authentication**
  - LoginActivity: Xử lý đăng nhập người dùng bằng email/password và Google
  - RegistrationActivity: Quản lý quá trình đăng ký người dùng

- **Movie Browsing**
  - MovieListActivity: Hiển thị danh sách phim có thể cuộn
  - MovieAdapter: RecyclerView adapter cho các item phim
  - MovieDetailActivity: Hiển thị thông tin chi tiết về một phim cụ thể

### Utils

- **RetrofitClient**: Singleton class quản lý giao tiếp API với xử lý token

## Kiến Trúc

Dự án tuân theo phiên bản đơn giản hóa của mô hình Repository:

- **UI Layer**: Activities và Adapters xử lý hiển thị UI và tương tác người dùng
- **Repository Layer**: Đóng vai trò trung gian giữa nguồn dữ liệu và UI
- **Remote Layer**: Quản lý giao tiếp API sử dụng Retrofit

## Dependencies

- **Retrofit**: Cho các request mạng HTTP
- **Gson**: Cho serialization/deserialization JSON
- **Glide**: Cho việc tải và cache hình ảnh
- **Firebase Authentication**: Cho đăng nhập Google
- **RecyclerView**: Cho việc hiển thị danh sách
- **Material Design Components**: Cho các element UI theo Material Design

## Hướng Dẫn Cài Đặt

### Yêu Cầu

- Android Studio Arctic Fox (2020.3.1) hoặc mới hơn
- JDK 11 hoặc mới hơn
- Android SDK 30+ (Target SDK)
- Minimum SDK: 21 (Android 5.0 Lollipop)

### Cấu Hình Firebase

1. Tạo dự án Firebase tại [Firebase Console](https://console.firebase.google.com/)
2. Thêm ứng dụng Android vào dự án Firebase
3. Tải xuống `google-services.json` và đặt trong thư mục app
4. Cấu hình Google Sign-In trong phần Firebase Authentication

### Cấu Hình API

Ứng dụng sử dụng RESTful API được host tại `https://prm-392-g2-cinema.vercel.app/`. Đảm bảo API có thể truy cập được.

### Cài Đặt

1. Clone repository:
   ```bash
   git clone https://github.com/<tên-người-dùng>/movie-booking-app.git
   ```

2. Mở dự án trong Android Studio

3. Sync dự án để tải dependencies

4. Build và chạy ứng dụng

## Tài Liệu API

### Authentication Endpoints

- `POST /auth/google`: Xác thực token Google
- `POST /api/auth/login`: Đăng nhập bằng email/password
- `POST /api/users`: Đăng ký người dùng

### Movie Endpoints

- `GET /api/movies`: Lấy danh sách phim có phân trang
- `GET /api/movies/{id}`: Lấy thông tin chi tiết về một phim cụ thể

## Luồng Hoạt Động Của Code

1. **Đăng Nhập/Đăng Ký**:
   - Người dùng mở ứng dụng → LoginActivity được hiển thị
   - Đăng nhập bằng email/password → AuthRepository.login() → ApiService.login() → Xử lý JWT token → lưu vào SharedPreferences
   - Đăng nhập Google → signInWithGoogle() → firebaseAuthWithGoogle() → lấy idToken → sendTokenToBackend() → ApiService.loginWithGoogle() → xử lý JWT token
   - Đăng ký → điền thông tin → AuthRepository.register() → ApiService.register() → quay lại LoginActivity

2. **Danh Sách Phim**:
   - Sau khi đăng nhập → MovieListActivity được hiển thị
   - fetchMovies() → MovieRepository.getMovies() → ApiService.getMovies() → handleMoviesResponse() → cập nhật vào adapter
   - Scroll đến cuối → tự động tải thêm trang tiếp theo (pagination)
   - Hiệu ứng scale: applyScaleEffect() được gọi khi scroll để tạo hiệu ứng

3. **Chi Tiết Phim**:
   - Double tap trên phim → Intent với MOVIE_ID → MovieDetailActivity
   - fetchMovieDetail() → MovieRepository.getMovieDetail() → ApiService.getMovieDetail() → hiển thị thông tin

4. **Xử Lý Token**:
   - RetrofitClient quản lý token qua SharedPreferences
   - AuthInterceptor thêm token vào mọi request
   - TokenAuthenticator xử lý lỗi 401 (unauthorized) → điều hướng về LoginActivity

## TODO List

- [x] Màn hình đăng nhập và đăng ký
- [x] Tích hợp đăng nhập Google
- [x] Hiển thị danh sách phim với phân trang
- [x] Hiệu ứng UI cho danh sách phim (scale effect)
- [x] Xem chi tiết phim
- [ ] Chọn rạp chiếu phim
- [ ] Chọn lịch chiếu
- [ ] Chọn ghế ngồi
- [ ] Thanh toán vé
- [ ] Quản lý vé đã đặt
- [ ] Thông báo đẩy khi có phim mới
- [ ] Đánh giá phim
- [ ] Tìm kiếm phim
- [ ] Lọc phim theo thể loại
- [ ] Quản lý thông tin cá nhân

## Những Phần Cần Hoàn Thiện

1. **Chức Năng Đặt Vé**:
   - Triển khai màn hình chọn rạp ("Theatres" trong bottom navigation)
   - Phát triển màn hình chọn lịch chiếu
   - Tạo màn hình chọn ghế ngồi
   - Triển khai tính năng thanh toán

2. **Quản Lý Vé**:
   - Phát triển màn hình "Tickets" từ bottom navigation
   - Hiển thị lịch sử đặt vé
   - Chức năng xem/hủy vé

3. **Tài Khoản Người Dùng**:
   - Phát triển màn hình "Profile" từ bottom navigation
   - Cho phép người dùng cập nhật thông tin cá nhân
   - Chức năng đổi mật khẩu

## Xử Lý Sự Cố

### Vấn Đề Thường Gặp

1. **Lỗi Xác Thực**
   - Kiểm tra cấu hình Firebase
   - Kiểm tra kết nối internet
   - Đảm bảo thông tin đăng nhập chính xác

2. **Không Tải Được Hình Ảnh**
   - Kiểm tra kết nối internet
   - Kiểm tra xem URL có thể truy cập được không
   - Đảm bảo Glide được khởi tạo đúng cách

3. **Vấn Đề Kết Nối API**
   - Kiểm tra BASE_URL trong RetrofitClient
   - Kiểm tra quyền internet trong manifest
   - Xác nhận trạng thái server API

## Đóng Góp

1. Fork repository
2. Tạo nhánh tính năng: `git checkout -b feature/tinh-nang-moi`
3. Commit thay đổi: `git commit -m 'Thêm tính năng mới'`
4. Push lên nhánh: `git push origin feature/tinh-nang-moi`
5. Tạo Pull Request

## Giấy Phép

Dự án này được cấp phép theo MIT License - xem file LICENSE để biết chi tiết.
