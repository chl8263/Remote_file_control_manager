const errorCodeToAlertCreater = (data) => {
    
    data.errors.forEach( x => {
        alert(x.message);
    });
}

export default errorCodeToAlertCreater;