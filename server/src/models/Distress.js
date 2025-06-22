const mongoose = require('mongoose');

const distressSchema = new mongoose.Schema({
  sender: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  recipients: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }], // all friends
  message: { type: String, required: true },
  location: {
    lat: { type: Number, required: true },
    lng: { type: Number, required: true }
  },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Distress', distressSchema);
