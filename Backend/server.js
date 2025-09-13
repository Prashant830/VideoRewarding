const express = require('express');
const app = express();
const ngrok = require('ngrok'); // add ngrok
const videoRout = require('./src/routes/videoRout');
const errorHandler = require('./src/middleware/errorHandler');

app.use(express.json());
app.use('/home', videoRout);
app.use(errorHandler);

const PORT = require('./src/config/config').PORT;

app.listen(PORT, async () => {
    console.log(`Server running on http://localhost:${PORT}`);

    try {
        const url = await ngrok.connect({
            addr: PORT,
            proto: 'http', // HTTP tunnel
        });
        console.log(`Ngrok tunnel running at: ${url}`);
    } catch (err) {
        console.error('Ngrok error:', err);
    }
});
