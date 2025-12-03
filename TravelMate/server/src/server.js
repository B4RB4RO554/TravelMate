const express = require('express');
const cors = require('cors');
const mongoose = require('mongoose');
require('dotenv').config();

const app = express();

// Middlewares
app.use(cors());
app.use(express.json());

// Environment variables
const PORT = process.env.PORT || 5000;
const MONGO_URI = process.env.MONGO_URI;
const JWT_SECRET = process.env.JWT_SECRET;

if (!MONGO_URI || !JWT_SECRET) {
  console.error("❌ Missing required environment variables in .env");
  process.exit(1);
}

// Connect to MongoDB
mongoose.connect(MONGO_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
}).then(() => console.log('✅ MongoDB connected'))
  .catch(err => {
    console.error('❌ MongoDB connection error:', err);
    process.exit(1);
  });

// Routes
const authRoutes = require('./routes/authRoutes');
const tripRoutes = require('./routes/tripRoutes');
const userRoutes = require('./routes/userRoutes');
const distressRoutes = require('./routes/distressRoutes');
const emergencyRoutes = require('./routes/emergencyRoutes');
const currencyRouter = require('./routes/currency');
const itineraryRoutes = require('./routes/itineraryRoutes');
const translateRoutes = require('./routes/translate');
const weatherRoutes = require('./routes/weatherRoutes');





app.use('/api/auth', authRoutes);
app.use('/api/trips', tripRoutes);
app.use('/api/users', userRoutes);
app.use('/api/distress', distressRoutes);
app.use('/api/emergency', emergencyRoutes);
app.use('/api/currency', currencyRouter);
app.use('/api/itinerary', itineraryRoutes);
app.use('/api/translate', translateRoutes);
app.use('/api/weather', weatherRoutes);

// Root route
app.get('/', (req, res) => {
  res.send('🌍 Welcome to the TravelMate API');
});

console.log('✅ Server started and /api/translate route registered');

// Start server
app.listen(PORT, () => {
  console.log(`🚀 Server is running on http://localhost:${PORT}`);
});
