const mongoose = require('mongoose');

const levelSchema = mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
      unique: true,
    },
    minScore: {
      type: Number,
      required: true,
    },
    maxScore: {
      type: Number,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model('Level', levelSchema);