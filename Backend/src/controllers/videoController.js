const AppError = require('../utils/AppError');
const videoService = require('../services/videoService.js');

async function getVideos(req, res, next) {
    try {
        const videos = await videoService.getVideos();
        res.status(200).json({
            status: 'success',
            videos: videos,
        });
    } catch (error) {
        next(error);
    }
}

module.exports = { getVideos};
