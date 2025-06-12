const asyncHandler = require('express-async-handler');
const Level = require('../models/Level');

/**
 * @desc    Obtener el nivel del usuario y los niveles disponibles
 * @route   GET /api/levels/:userUid/level
 * @access  Public (o puedes cambiarlo a Private si hay autenticación)
 */
const getUserLevel = asyncHandler(async (req, res) => {
  const userScore = parseInt(req.query.score);

  if (isNaN(userScore) || userScore < 0) {
    res.status(400);
    throw new Error('Por favor, proporciona un score válido (número positivo).');
  }


  const allLevels = await Level.find({}).sort({ minScore: 1 });

  if (!allLevels || allLevels.length === 0) {
    res.status(404);
    throw new Error('No se han encontrado niveles en la base de datos.');
  }

  let currentUserLevel = null;
  for (let i = allLevels.length - 1; i >= 0; i--) {
    if (userScore >= allLevels[i].minScore) {
      currentUserLevel = allLevels[i];
      break;
    }
  }

  if (!currentUserLevel) {
    currentUserLevel = allLevels[0];
  }


  const availableLevels = allLevels.map(level => ({
    ...level.toObject(),
    isUsable: userScore >= level.minScore,
  }));

  res.status(200).json({
    userScore: userScore,
    currentUserLevel: currentUserLevel,
    allLevels: availableLevels,
  });
});

module.exports = {
    getUserLevel,
};