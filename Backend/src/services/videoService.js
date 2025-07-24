const videoModel = require('../models/videoModel');
const AppError = require('../utils/AppError');

async function getVideos() {
    try {
        const videos = await videoModel.getAllVideos();
        if (!videos) throw new AppError('Videos not found', 401);
        return videos;
    } catch (error) {
        throw new AppError(error.message, 401);
    }
}



module.exports = { getVideos};
