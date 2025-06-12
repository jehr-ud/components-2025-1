const Level = require('../models/Level'); 

/**
 * @desc    Inicializa los niveles por defecto en la base de datos si no existen.
 * Niveles: A1, B1, B2, C1 con sus respectivos minScore.
 */
const initializeLevels = async () => {
  try {
    const existingLevels = await Level.find({});

    if (existingLevels.length === 0) {
      console.log('No se encontraron niveles. Creando niveles por defecto...');

      const defaultLevels = [
        { name: 'A1', minScore: 0 },
        { name: 'B1', minScore: 100 },
        { name: 'B2', minScore: 250 },
        { name: 'C1', minScore: 500 },
      ];

      await Level.insertMany(defaultLevels);
      console.log('Niveles por defecto creados exitosamente.');
    } else {
      console.log('Los niveles ya existen en la base de datos.');
    }
  } catch (error) {
    console.error('Error al inicializar los niveles:', error.message);
  }
};

module.exports = initializeLevels;