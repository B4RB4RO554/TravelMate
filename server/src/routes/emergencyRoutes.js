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

// GET /api/emergency/places?lat=36.8&lng=10.1
router.get('/places', async (req, res) => {
    try {
      const { lat, lng } = req.query;
  
      if (!lat || !lng) {
        return res.status(400).json({ error: 'lat and lng are required' });
      }
  
      const apiKey = process.env.GEOAPIFY_API_KEY;
      if (!apiKey) {
        return res.status(500).json({ error: 'Geoapify API key not configured' });
      }
  
      console.log('Using Geoapify API Key:', apiKey);
  
      const categories = 'healthcare.hospital,healthcare.clinic_or_praxis,service.police,service.vehicle.fuel';
      const url = `https://api.geoapify.com/v2/places?categories=${categories}&filter=circle:${lng},${lat},5000&bias=proximity:${lng},${lat}&limit=10&apiKey=${apiKey}`;

        
      const { data } = await axios.get(url);
  
      const results = data.features.map(place => ({
        name: place.properties.name,
        address: place.properties.formatted,
        lat: place.geometry.coordinates[1],
        lng: place.geometry.coordinates[0]
      }));
  
      res.json(results);
    } catch (err) {
      console.error('❌ Error fetching places:', err.response?.data || err.message);
      res.status(500).json({ error: 'Failed to fetch places', details: err.response?.data || err.message });
    }
  });
  

module.exports = router;
