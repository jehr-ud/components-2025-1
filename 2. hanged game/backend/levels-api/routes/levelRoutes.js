
const express = require('express');
const router = express.Router();
const {
  getUserLevel
} = require('../controllers/levelController');

router.get('/user', getUserLevel);

module.exports = router;