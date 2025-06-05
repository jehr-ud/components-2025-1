const mongoose = require('mongoose');

const userSchema = mongoose.Schema(
  {
    username: {
      type: String,
      required: true,
      unique: true,
    },
    email: {
      type: String,
      required: true,
      unique: true,
    },
    currentLevel: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Level',
      required: true,
    },
    score: {
      type: Number,
      default: 0,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model('User', userSchema);