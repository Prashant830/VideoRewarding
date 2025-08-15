const AppError = require('../utils/AppError');

async function getAllVideos() {
    try {
        const videos = [
            {
                url: "https://youtube.com/shorts/bo3QncEebJ8?si=QENZjWz51QgqArB3",
                totalRuntime: 53 
            },
            {
                url: "https://youtube.com/shorts/dTanksRR8Ac?si=d-F11cekwwoBxgqR",
                totalRuntime: 57
            },
            {
                url: "https://youtube.com/shorts/lYktrLTfIY4?si=t5Lpw7ctTxlDCUkc",
                totalRuntime: 47
            },
            {
                url: "https://youtube.com/shorts/1vh0tt2lKy8?si=84uL40e9OPuWqIj7",
                totalRuntime: 43
            },
            {
                url: "https://youtube.com/shorts/XAidJjuh5gk?si=fZPlWxrCTmtRnzg_",
                totalRuntime: 47
            }
        ];

        return videos;
    } catch (err) {
        console.error('Error in token generation:', err.message);
        throw new AppError(err.message, 401);
    }
}

module.exports = { getAllVideos };
