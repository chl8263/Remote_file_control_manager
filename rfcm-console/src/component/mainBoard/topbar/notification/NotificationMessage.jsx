import React from "react";

const APPROVE = "APPROVE";
const UPDATE = "UPDATE";
const REJECT = "REJECT";

const NotificationMessage = ( { notiInfo } ) => {
    return(
        <>
            <a href="javascript:void(0)" className="link border-top">
                <div className="d-flex no-block align-items-center p-10">
                    {(notiInfo.status === APPROVE) && <i className="mdi mdi-check" style={{"margin-left": "5px", width: 20}}></i> }
                    {(notiInfo.status === UPDATE) && <i className="mdi mdi-update" style={{"margin-left": "5px", width: 20}}></i> }
                    {(notiInfo.status === REJECT) && <i className="mdi mdi-close" style={{"margin-left": "5px", width: 20}}></i> }
                    <div className="m-l-10">
                        <span className="mail-desc">{ notiInfo.message }</span>
                    </div>
                </div>
            </a>
        </>
    );
}

export default NotificationMessage;