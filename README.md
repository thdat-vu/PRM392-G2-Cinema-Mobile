# Movie Booking App

A simple Android application that allows users to log in with Google, browse a list of movies, view detailed movie information, select showtimes, and book tickets. The app leverages Firebase for data management and authentication.

## Main Features
- **Google Login**: Authenticate users via Firebase Authentication using Google Sign-In.
- **Movie List**: Display a paginated list of movies from Firestore in a horizontal RecyclerView with a banner slider.
- **Movie Details**: View detailed movie information (title, description, genres, etc.) by double-tapping a movie in the list.
- **Showtime Selection**: Browse available showtimes by date and theater, with location-based sorting.
- **Booking**: Create bookings with seat selection, food/drink options, and payment status tracking.
- **Profile Management**: Update user information (name, phone) and log out.
- **Booking History**: View a list of past bookings.

## Technologies Used
- **Language**: Java
- **Framework**: Android SDK
- **Database**: Firebase Firestore
- **Authentication**: Firebase Authentication (Google Sign-In)
- **Image Loading**: Glide (for movie banners and theater logos)
- **Location Services**: Google Play Services (FusedLocationProviderClient for theater distance calculation)
- **Asynchronous Programming**: CompletableFuture for fetching food/drink items

## Setup Instructions
1. **Clone the repository**:
   ```
   git clone <repository_url>
   ```
2. **Add Firebase configuration**:
   - Download the `google-services.json` file from the Firebase Console.
   - Place it in the `app/` directory of the project.
3. **Sync the project with Gradle**:
   ```
   ./gradlew build
   ```
4. **Run the app**:
   - Launch the app on an Android emulator or a physical device via Android Studio.

## Directory Structure
```
com.g2.moviebooking
├── data
│   ├── model
│   │   ├── Booking.java     # Booking model with showtime, seats, payment details
│   │   ├── Movie.java       # Movie model with title, description, genres, etc.
│   │   ├── Showtime.java    # Showtime model with theater, time, and seat availability
│   │   ├── Theatre.java     # Theater model with name, address, location coordinates
│   │   └── User.java        # User model with name, email, and phone
│   └── repository
│       ├── AuthRepository.java      # Handles Google login and user data storage
│       ├── BookingRepository.java   # Manages booking creation and updates
│       ├── FoodAndDrinkRepository.java # Fetches food/drink options
│       ├── MovieRepository.java     # Queries movie list and details
│       ├── ShowtimeRepository.java  # Retrieves showtimes and updates seat availability
│       └── TheatreRepository.java   # Fetches theater information
├── ui
│   ├── auth
│   │   └── LoginActivity.java       # Google login screen
│   ├── MovieAdapter.java            # Adapter for RecyclerView movie list
│   ├── MovieDetailActivity.java     # Displays movie details
│   ├── MovieListActivity.java       # Movie list with pagination and search
│   ├── ProfileActivity.java         # User profile management
│   ├── ShowtimeSelectionActivity.java # Showtime selection by date and theater
│   └── bookings
│       └── BookingHistoryListActivity.java # Displays booking history
└── utils
    ├── Constants.java               # App-wide constants (e.g., PAGE_SIZE)
    ├── FirebaseClient.java          # Singleton for FirebaseAuth and Firestore
    └── LocationUtils.java           # Utility for fetching device location
```

## How It Works
1. **LoginActivity**:
   - Uses Google Sign-In for authentication.
   - Saves user data (name, email) to Firestore upon successful login.
2. **MovieListActivity**:
   - Loads movies from Firestore with pagination (10 movies per page).
   - Displays movies in a horizontal RecyclerView with a PagerSnapHelper and a banner slider.
   - Supports search by movie title and automatic loading of additional movies on scroll.
3. **MovieDetailActivity**:
   - Displays detailed movie information (title, description, genres, etc.) when a movie is double-tapped.
   - Uses Glide to load movie banners.
4. **ShowtimeSelectionActivity**:
   - Fetches showtimes for a selected movie, filtered by date.
   - Displays theaters with available showtimes, sorted by distance using the device's location.
5. **Booking Process**:
   - Users select seats and optional food/drink items for a showtime.
   - BookingRepository creates a booking with a unique code and calculates the total amount.
   - Payment status is updated upon confirmation (e.g., "PENDING" to "PAID").
6. **ProfileActivity**:
   - Displays and allows editing of user information (name, phone).
   - Handles logout functionality.
7. **BookingHistoryListActivity**:
   - Shows a list of the user's past bookings, ordered by booking date.

## TODO
- [ ] Integrate a payment gateway (e.g., Stripe, PayPal) for completing transactions.
- [ ] Add movie filters by genre, rating, or release date in MovieListActivity.
- [ ] Enhance UI with animations for screen transitions and support for dark mode.
- [ ] Implement network error handling and offline storage using Room or SharedPreferences.
- [ ] Add theater search and filtering options in ShowtimeSelectionActivity.

## Suggestions
- **API Integration**: Consider integrating a third-party API like The Movie Database (TMDb) to enrich movie data with trailers, reviews, and additional metadata.
- **Enhanced Booking**: Add features like seat selection previews, real-time seat availability updates, and cancellation options.
- **User Experience**: Implement push notifications for booking confirmations and upcoming showtime reminders.
