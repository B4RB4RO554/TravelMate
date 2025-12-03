const express = require('express');
const router = express.Router();
const User = require('../models/User');
const Distress = require('../models/Distress');
const auth = require('../middleware/auth');

// Send distress signal to all friends
router.post('/', auth, async (req, res) => {
    try {
      console.log('🔔 /api/distress route hit');
  
      const { message, lat, lng } = req.body;
  
      if (!message || lat == null || lng == null) {
        console.log('❌ Missing fields');
        return res.status(400).json({ error: 'Message, lat, and lng are required' });
      }
  
      const sender = await User.findById(req.user.id).populate('friends');
      if (!sender) {
        console.log('❌ Sender not found');
        return res.status(404).json({ message: 'User not found' });
      }
  
      if (!sender.friends || sender.friends.length === 0) {
        console.log('⚠️ No friends found for sender');
      }
  
      const recipientIds = sender.friends.map(friend => friend._id);
      console.log('👥 Recipients:', recipientIds);
  
      const distress = new Distress({
        sender: sender._id,
        recipients: recipientIds,
        message,
        location: { lat, lng }
      });
  
      console.log('📝 Attempting to save distress signal...');
      await distress.save();
      console.log('✅ Distress signal saved to DB');
  
      res.status(201).json({ message: 'Distress signal sent' });
  
    } catch (err) {
      console.error('❌ Error in distress signal:', err.message);
      res.status(500).json({ error: 'Server error' });
    }
  });
  

// Get distress signals received by current user
router.get('/', auth, async (req, res) => {
  try {
    console.log('🔔 GET /api/distress called by user:', req.user.id);

    const signals = await Distress.find({ recipients: req.user.id })
      .populate('sender', 'firstName lastName username')
      .sort({ createdAt: -1 });

    res.json(signals);
  } catch (err) {
    console.error('❌ Error fetching distress signals:', err.message);
    res.status(500).json({ error: 'Server error' });
  }
});

module.exports = router;
