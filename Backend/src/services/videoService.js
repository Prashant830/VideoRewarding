const videoModel = require('../models/videoModel');
const AppError = require('../utils/AppError');

async function getVideos() {
    try {
        const videoUrls = await videoModel.getAllVideos(); // direct call for now, or videoModel.getAllVideos();
        if (!videoUrls || videoUrls.length === 0) {
            throw new AppError('Videos not found', 401);
        }

        // Convert to desired schema
        const videos = videoUrls.map((url, index) => ({
            videoId: index + 1,
            videoUrl: url.url,
            currentWatched: 0,
            totalRuntime: url.totalRuntime
        }));

        return videos;
    } catch (error) {
        throw new AppError(error.message, 401);
    }
}

module.exports = { getVideos };
