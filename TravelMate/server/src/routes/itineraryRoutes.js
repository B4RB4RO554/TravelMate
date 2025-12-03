const express = require('express');
const router = express.Router();
const axios = require('axios');
require('dotenv').config();

// GET /api/itinerary/route?startLat=36.8&startLng=10.1&endLat=36.9&endLng=10.2
router.get('/route', async (req, res) => {
  try {
    const { startLat, startLng, endLat, endLng } = req.query;

    if (!startLat || !startLng || !endLat || !endLng) {
      return res.status(400).json({ error: 'All coordinates are required' });
    }

    const apiKey = process.env.GEOAPIFY_API_KEY;
    if (!apiKey) {
      return res.status(500).json({ error: 'Geoapify API key missing in .env' });
    }

    const url = `https://api.geoapify.com/v1/routing?waypoints=${startLat},${startLng}|${endLat},${endLng}&mode=drive&apiKey=${apiKey}`;

    const { data } = await axios.get(url);

    const route = data.features[0].geometry.coordinates.map(coord => ({
      lat: coord[1],
      lng: coord[0]
    }));

    res.json({ route });
  } catch (err) {
    console.error('❌ Error fetching route:', err.response?.data || err.message);
    res.status(500).json({ error: 'Failed to fetch route', details: err.message });
  }
});

module.exports = router;
