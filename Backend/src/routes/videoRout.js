const express = require('express');
const VideoController = require('../controllers/videoController');

const router = express.Router();

// get videos
router.get('/videos', VideoController.getVideos);

// get contract address
router.get('/balance', VideoController.getBalance);

// send reward to video viewer 
router.post('/sendReward', VideoController.sendReward);

// deposite ether in contract.
router.post('/deposit', VideoController.depositBalance);

module.exports = router;
