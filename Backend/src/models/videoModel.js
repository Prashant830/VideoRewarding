const AppError = require('../utils/AppError');

async function getAllVideos() {
    try {
        const videos = ["https://youtube.com/shorts/bo3QncEebJ8?si=QENZjWz51QgqArB3", 
                         "https://youtube.com/shorts/dTanksRR8Ac?si=d-F11cekwwoBxgqR" ,
                         "https://youtube.com/shorts/lYktrLTfIY4?si=t5Lpw7ctTxlDCUkc" ,
                         "https://youtube.com/shorts/1vh0tt2lKy8?si=84uL40e9OPuWqIj7" ,
                         "https://youtube.com/shorts/XAidJjuh5gk?si=fZPlWxrCTmtRnzg_" ,
                         "https://youtube.com/shorts/8453RdTR_hk?si=yR-BnDKFDC74iRFz" ,
                         "https://youtube.com/shorts/DmVCSeWbl8I?si=2eRCKOe5p_td7lA0" ,
                         "https://youtube.com/shorts/VpOyHbGV7sw?si=W3i0WNmWe3P2_-lB" ,
                         "https://youtube.com/shorts/aXK3hAYxwPA?si=NBFN9Uu5SA4gEHqH" ,
                         "https://youtube.com/shorts/88QAcMEFYes?si=xQKehkl529Dp2-SS" ]
        return videos;
    } catch (err) {
        console.error('Error in token generation:', err.message); 
        throw new AppError(err.message, 401);
    } 
}


module.exports = { getAllVideos };
