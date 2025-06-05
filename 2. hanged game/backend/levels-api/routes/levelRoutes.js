
const express = require('express');
const router = express.Router();
const {
  getLevels,
  getUserLevel,
  updateScoreAndLevel,
} = require('../controllers/levelController');

router.get('/', getLevels);

router.get('/:userId/level', getUserLevel);
router.put('/:userId/score', updateScoreAndLevel);

module.exports = router;