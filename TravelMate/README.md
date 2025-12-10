# TravelMate

> **Note**: This is a school project created as an educational exercise to demonstrate full-stack mobile application development with Android frontend and Node.js backend.

A comprehensive travel companion mobile application that helps users manage trips, stay safe, connect with friends, and explore new destinations.

## Overview

TravelMate is an Android application built with Kotlin and Jetpack Compose, backed by a Node.js/Express API. The app provides seamless travel planning, emergency services integration, real-time location sharing, and social connectivity features. It features an offline-first architecture ensuring functionality even without internet connection.

## Key Features

### 🗺️ **Trip Management**
- Create, edit, and delete trips with destination, dates, and notes
- Share trips with friends
- Offline access to trip history with automatic sync
- View shared trips from friends

### 📍 **Maps & Navigation**
- Native map integration using OpenStreetMap (OSMDroid)
- Real-time location tracking
- Route planning with distance and duration estimates
- Interactive map controls (zoom, current location)
- Mark destinations and view routes

### 🚨 **Emergency Features**
- **Distress Signal (SOS)**: Send emergency alerts to all friends with your location
- **Emergency Services**: Find nearby hospitals, police stations, fuel stations, and other critical services
- **Emergency Numbers**: Quick access to emergency phone numbers by country
- One-tap calling to emergency services

### 👥 **Social Features**
- Search and connect with other users
- Friend request system (send, accept, reject)
- Manage your friend list
- Location sharing when sending distress signals
- Share trips with specific friends

### 🔔 **Notifications**
- Real-time notification system
- SOS alerts from friends
- Friend request notifications
- Trip sharing notifications
- Read/unread status tracking

### 💱 **Currency Converter**
- Real-time currency conversion
- Support for all major currencies
- Live exchange rates from external API

### 🌍 **Additional Features**
- User profiles with customizable information
- Cultural travel guides and tips
- Points of Interest (POI) suggestions
- Offline mode with automatic sync
- Secure authentication with JWT tokens

## Technology Stack

### Frontend
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Local Database**: Room (SQLite)
- **Networking**: Retrofit 2 + OkHttp3
- **Maps**: OSMDroid
- **Async**: Kotlin Coroutines
- **State Management**: MutableStateFlow
- **Permissions**: Accompanist
- **Location**: Google Play Services
- **Background Sync**: WorkManager
- **Preferences**: DataStore

### Backend
- **Runtime**: Node.js
- **Framework**: Express.js 5.x
- **Database**: MongoDB with Mongoose
- **Authentication**: JWT (2-hour expiry)
- **Password Security**: Bcryptjs (10-round hashing)
- **HTTP Client**: Axios

### External APIs
- **Places & Locations**: Geoapify API
- **Currency Exchange**: ExchangeRate API
- **Routing & Directions**: Project OSRM
- **Location Services**: Google Play Services

## Architecture

### Offline-First Design
- All data is cached locally using Room database
- App loads cached data first for instant UI response
- Automatically syncs with server when online
- Periodic background sync every 15 minutes using WorkManager
- Full offline functionality for critical features

### Data Flow
```
UI Layer (Compose Screens)
    ↓
ViewModel (State Management)
    ↓
Repository (Data Access Logic)
    ↓
Network (Retrofit API) + Database (Room)
```

### Security
- JWT-based authentication with 2-hour token expiry
- Bcrypt password hashing (10 rounds)
- Strong password validation (8+ chars, mixed case, numbers, special characters)
- User data isolation (users can only access their own data)
- CORS-enabled API for secure cross-origin requests

## Project Structure

```
TravelMate/
├── android/                              # Android app
│   └── app/src/main/
│       ├── java/tn/bidpaifusion/travelmatekotlin/
│       │   ├── data/
│       │   │   ├── api/                 # Retrofit services
│       │   │   ├── models/              # Data classes
│       │   │   ├── local/               # Room database
│       │   │   └── repository/          # Data access layer
│       │   ├── ui/
│       │   │   ├── home/                # Home screen
│       │   │   ├── login/               # Authentication
│       │   │   ├── trip/                # Trip management
│       │   │   ├── map/                 # Map screen
│       │   │   ├── screens/             # Feature screens
│       │   │   └── navigation/          # Navigation graph
│       │   ├── viewmodel/               # State management
│       │   ├── util/                    # Utilities
│       │   └── TravelMateApplication.kt # App initialization
│       └── AndroidManifest.xml
│
├── server/                               # Node.js backend
│   ├── src/
│   │   ├── models/                      # MongoDB schemas
│   │   ├── routes/                      # API endpoints
│   │   ├── middleware/                  # Authentication, validation
│   │   └── server.js                    # Express app setup
│   ├── package.json
│   └── .env                             # Environment configuration
│
├── README.md                             # This file
├── .gitignore                            # Git ignore rules
└── package.json                          # Project dependencies (if applicable)
```

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Node.js 18.x or later
- MongoDB (local or cloud instance)
- Android device/emulator (Min SDK 24, Target SDK 34)

### Backend Setup
```bash
cd server
npm install

# Create .env file with:
MONGODB_URI=your_mongodb_connection_string
JWT_SECRET=your_jwt_secret
GEOAPIFY_KEY=your_geoapify_api_key
EXCHANGE_RATE_KEY=your_exchange_rate_api_key
PORT=5000
```

```bash
npm start
```

### Frontend Setup
1. Open `android/` in Android Studio
2. Sync Gradle files
3. Update API base URL in code if needed (default: http://10.0.2.2:5000/api)
4. Run on device/emulator

## Database Schema

### MongoDB Collections

**Users**
- Email, username, password (hashed)
- First name, last name, phone, address, birth date
- Profile picture, friend list
- Timestamps

**Trips**
- User ID, destination, start/end dates, notes
- Shared with (array of user IDs)
- Timestamps

**Friends**
- Requester, recipient user IDs
- Status (pending, accepted, rejected)
- Timestamps

**Distress Signals**
- Sender user ID, recipients array
- Location (latitude, longitude)
- Message, timestamp

**Notifications**
- Recipient user ID
- Type (sos, friend_request, trip_shared, etc.)
- Title, message, data
- Read status, timestamp

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - User login

### Users
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update profile
- `PUT /api/users/me/password` - Change password

### Trips
- `POST /api/trips` - Create trip
- `GET /api/trips` - List user trips
- `PUT /api/trips/:id` - Update trip
- `DELETE /api/trips/:id` - Delete trip
- `GET /api/trips/shared` - Get shared trips
- `POST /api/trips/:id/share` - Share trip with friend

### Friends
- `GET /api/friends/search` - Search users
- `POST /api/friends/request` - Send friend request
- `GET /api/friends/requests/received` - Get pending requests
- `PUT /api/friends/request/:id/accept` - Accept request
- `PUT /api/friends/request/:id/reject` - Reject request
- `GET /api/friends` - Get all friends
- `DELETE /api/friends/:friendshipId` - Remove friend

### Distress & Emergency
- `POST /api/distress` - Send SOS signal
- `GET /api/distress` - Get received SOS signals
- `GET /api/emergency/numbers` - Get emergency phone numbers
- `GET /api/emergency/places` - Find nearby services

### Other Features
- `GET /api/currency/convert` - Currency conversion
- `GET|PUT|DELETE /api/notifications` - Notification management
- `POST /api/translate` - Text translation

## Features in Detail

### Trip Management
Users can create trips with destination, dates, and notes. Trips are stored locally and synced with the server. Users can share trips with friends who can then view the shared trips in their app.

### Offline Functionality
The app uses an offline-first architecture:
1. Data is cached locally in Room database
2. UI displays cached data immediately
3. App fetches fresh data from server if online
4. New data is stored in local database
5. Background sync (every 15 minutes) keeps data updated

### Emergency System
The distress signal feature allows users to send an SOS to all their friends with their current location. Friends receive a notification and can view the user's location on the map.

Emergency services can be found nearby using location data and the Geoapify API. Users can get emergency numbers for any country.

### Social Network
Friends can connect with each other through a request system. Once accepted, friends can see each other's distress signals and shared trips.

### Notifications
The notification system keeps users informed about friend requests, SOS signals from friends, and trip shares. Notifications are stored in the database and displayed in the app.

## Security Considerations

- **Password Security**: Strong validation and bcryptjs hashing
- **Authentication**: JWT tokens with automatic expiry
- **Authorization**: Users can only access their own data
- **API Security**: CORS configuration for safe cross-origin requests
- **Data Encryption**: Passwords hashed on backend

## Future Enhancements

- Push notifications for real-time alerts
- Advanced trip itinerary planning
- Expense tracking and splitting
- Weather integration for destinations
- Video call emergency help
- Travel insurance integration
- Local guide marketplace
- AI-powered travel recommendations

## Troubleshooting

### Connection Issues
- Verify API base URL is correct for your environment
- Check MongoDB connection string
- Ensure all required API keys are configured

### Location Issues
- Grant location permissions on Android device
- Ensure device location services are enabled
- Check if Google Play Services is installed

### Offline Mode
- Cached data appears first
- Manual sync can be triggered on login
- Background sync runs every 15 minutes

## Contributing

This is a collaborative project. Please follow the existing code structure and patterns when making changes.

## License

This project is proprietary software. All rights reserved.
