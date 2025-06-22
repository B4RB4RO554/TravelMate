const express = require('express');
const router = express.Router();
const Trip = require('../models/Trip');
const auth = require('../middleware/auth');

// POST /api/trips — Create a new trip
router.post('/', auth, async (req, res) => {
  try {
    const trip = new Trip({ ...req.body, userId: req.user.id });
    await trip.save();
    res.status(201).json(trip);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /api/trips — Get all trips for logged-in user
router.get('/', auth, async (req, res) => {
  try {
    const trips = await Trip.find({ userId: req.user.id });
    res.json(trips);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// POST /api/trips/:id/share — Share a trip with another user
router.post('/:id/share', auth, async (req, res) => {
  try {
    const { friendUserId } = req.body;
    const trip = await Trip.findOne({ _id: req.params.id, userId: req.user.id });

    if (!trip) return res.status(404).json({ message: 'Trip not found or unauthorized' });

    if (!trip.sharedWith.includes(friendUserId)) {
      trip.sharedWith.push(friendUserId);
      await trip.save();
    }

    res.json({ message: 'Trip shared successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// GET /api/trips/shared — Trips shared with current user
router.get('/shared', auth, async (req, res) => {
  try {
    const trips = await Trip.find({ sharedWith: req.user.id });
    res.json(trips);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});


module.exports = router;
