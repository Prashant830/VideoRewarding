const AppError = require('../utils/AppError');

async function getAllVideos() {
    try {
        const videos = ["https://youtube.com/shorts/bo3QncEebJ8?si=QENZjWz51QgqArB3", 
                         "https://youtube.com/shorts/dTanksRR8Ac?si=d-F11cekwwoBxgqR" ,
                         "https://youtube.com/shorts/lYktrLTfIY4?si=t5Lpw7ctTxlDCUkc" ,
                         "https://youtube.com/shorts/1vh0tt2lKy8?si=84uL40e9OPuWqIj7" ,
                         "https://youtube.com/shorts/XAidJjuh5gk?si=fZPlWxrCTmtRnzg_"
                        ]
        return videos;
    } catch (err) {
        console.error('Error in token generation:', err.message); 
        throw new AppError(err.message, 401);
    } 
}


module.exports = { getAllVideos };
