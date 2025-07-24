function AppError(message, statusCode) {
    const error = new Error(message);

    if (statusCode === 201) {
        error.statusCode = statusCode;
        error.status = 'success';
        error.isOperational = true;
    } else {
        error.statusCode = statusCode;
        error.status = statusCode >= 500 ? 'error' : 'fail';
        error.isOperational = true;
    }

    return error;
}


module.exports = AppError;
