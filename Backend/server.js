const express = require('express');
const app = express();
const videoRout = require('./src/routes/videoRout');

const errorHandler = require('./src/middleware/errorHandler');

app.use(express.json());

app.use('/home', videoRout);

app.use(errorHandler);

const PORT = require('./src/config/config').PORT;

app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
