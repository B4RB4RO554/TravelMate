const express = require('express');
const router = express.Router();
const User = require('../models/User');
const auth = require('../middleware/auth');

// POST /api/users/:id/connect — connect with another user
router.post('/:id/connect', auth, async (req, res) => {
  try {
    const friendId = req.params.id;

    if (req.user.id === friendId) {
      return res.status(400).json({ message: "You can't connect to yourself" });
    }

    const me = await User.findById(req.user.id);
    const friend = await User.findById(friendId);

    if (!friend) return res.status(404).json({ message: "Friend not found" });

    if (!me.friends.includes(friendId)) {
      me.friends.push(friendId);
      await me.save();
    }

    res.json({ message: 'Friend connected successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
