const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  firstName: {
    type: String,
    required: true,
    minlength: 2
  },
  lastName: {
    type: String,
    required: true,
    minlength: 2
  },
  birthDate: {
    type: Date,
    required: true
  },
  email: {
    type: String,
    required: true,
    unique: true,
    match: /.+\@.+\..+/ // basic email pattern
  },
  phone: {
    type: String,
    required: true,
    match: /^\+?[0-9]{7,15}$/ // allows + and digits
  },
  address: {
    type: String,
    required: true
  },
  profilePicture: {
    type: String, // could be a base64 string or a URL
    default: ""
  },
  username: {
    type: String,
    required: true,
    unique: true,
    minlength: 3
  },
  password: {
    type: String,
    required: true
  },
  friends: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }]
}, {
  timestamps: true // adds createdAt and updatedAt
});

module.exports = mongoose.model('User', userSchema);
