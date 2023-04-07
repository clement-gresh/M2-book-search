export const showMessage = (status:number|string) : string => {
    let message:string = "";
    switch (status) {
        case 400:
            message = "Request Error(400)";
            break;
        case 401:
            message = "Relogin(401)";
            break;
        case 403:
            message = "Rejection(403)";
            break;
        case 404:
            message = "Request Error(404)";
            break;
        case 408:
            message = "Request Runout of time(408)";
            break;
        case 500:
            message = "Server Error(500)";
            break;
        case 501:
            message = "(501)";
            break;
        case 502:
            message = "Connection Error(502)";
            break;
        case 503:
            message = "Service Not Working(503)";
            break;
        case 504:
            message = "Connection Runout of time(504)";
            break;
        case 505:
            message = "Not support this version of HTTP(505)";
            break;
        default:
            message = `Error Connection(${status})!`;
    }
    return `${message}，Contact admin！`;
};

