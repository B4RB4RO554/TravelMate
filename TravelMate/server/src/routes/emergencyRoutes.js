const express = require('express');
const router = express.Router();
const emergencyNumbers = require('../data/emergencyNumbers');
const axios = require('axios');

// GET /api/emergency/numbers?country=TN
router.get('/numbers', (req, res) => {
  const code = req.query.country?.toUpperCase();

  if (!code) {
    return res.status(400).json({ error: 'Country code is required, e.g., ?country=TN' });
  }

  const numbers = emergencyNumbers[code];
  if (!numbers) {
    return res.status(404).json({ error: 'Emergency numbers not found for this country' });
  }

  res.json(numbers);
});

// GET /api/emergency/places?lat=36.8&lng=10.1&type=hospital
router.get('/places', async (req, res) => {
    try {
      const { lat, lng, type } = req.query;
  
      if (!lat || !lng) {
        return res.status(400).json({ error: 'lat and lng are required' });
      }
  
      const apiKey = process.env.GEOAPIFY_API_KEY;
      if (!apiKey) {
        return res.status(500).json({ error: 'Geoapify API key not configured' });
      }
  
      // Map type to Geoapify categories
      let categories;
      switch (type) {
        case 'restaurant':
          categories = 'catering.restaurant,catering.fast_food,catering.cafe';
          break;
        case 'hotel':
          categories = 'accommodation.hotel,accommodation.guest_house,accommodation.hostel';
          break;
        case 'attraction':
          categories = 'tourism.attraction,tourism.sights,entertainment.museum';
          break;
        case 'hospital':
        case 'police':
        default:
          categories = 'healthcare.hospital,healthcare.clinic_or_praxis,service.police,service.vehicle.fuel';
          break;
      }
  
      const url = `https://api.geoapify.com/v2/places?categories=${categories}&filter=circle:${lng},${lat},5000&bias=proximity:${lng},${lat}&limit=15&apiKey=${apiKey}`;
        
      const { data } = await axios.get(url);
  
      const results = data.features.map(place => ({
        name: place.properties.name || 'Unknown',
        address: place.properties.formatted || place.properties.street || 'No address',
        lat: place.geometry.coordinates[1],
        lon: place.geometry.coordinates[0],
        phone: place.properties.contact?.phone || null,
        category: place.properties.categories?.[0] || type
      }));
  
      res.json(results);
    } catch (err) {
      console.error('❌ Error fetching places:', err.response?.data || err.message);
      res.status(500).json({ error: 'Failed to fetch places', details: err.response?.data || err.message });
    }
  });
  

module.exports = router;
