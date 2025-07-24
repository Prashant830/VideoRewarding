const express = require('express');
const VideoController = require('../controllers/videoController');

const router = express.Router();

router.get('/videos', VideoController.getVideos);

module.exports = router;
