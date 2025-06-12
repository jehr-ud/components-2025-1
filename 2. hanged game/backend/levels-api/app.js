
const express = require('express');
const dotenv = require('dotenv');
const connectDB = require('./config/db');
const levelRoutes = require('./routes/levelRoutes');
const initializeLevels = require('./data/levelSeeder');


dotenv.config();


connectDB();

const app = express();

app.use(express.json());

app.use('/api/levels', levelRoutes);

const PORT = process.env.PORT || 5000;

app.listen(PORT, async () => {
  console.log(`Server running on port ${PORT}`);

  await initializeLevels();

});