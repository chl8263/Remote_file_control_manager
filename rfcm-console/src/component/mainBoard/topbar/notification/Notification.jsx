import React, { useState, useEffect } from "react";

import NotificationMessage from "../notification/NotificationMessage";
import { connect } from "react-redux";
import { PAGE_ROUTE, HTTP, MediaType, COLOR} from "../../../../util/Const";


const Notification = ( { appInfo } ) => {

    const [notificationList, setNotificationList] = useState([]);

    useEffect(() => {
        getNotication();
    }, []);

    const getNotication = () => {
        const currentAccountId = appInfo.userInfo.currentUserId;
        const JWT_TOKEN = appInfo.appInfo.jwtToken;

        //s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + `/api/accounts/${currentAccountId}/accountNotifications?offset=0&limit=9`, {
            method: HTTP.GET,
            headers: {
                'Content-type': MediaType.JSON,
                'Accept': MediaType.HAL_JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
            },
        }).then(res => {
            return res.json();
        }).then(res => {
            if("errors" in res){
                try{
                    errorCodeToAlertCreater(json);
                }catch(error){
                    throw error;
                }
            }else if("_embedded" in res){
                setNotificationList(res._embedded.accountNotificationEntityModelList);
            }
        }).catch(error => {
            console.error(error);
            alert("Client unexpect error.");
        });
        // e: Ajax ----------------------------------
    }

    return (
        <>
            <li className="nav-item dropdown">
                <a onClick={getNotication} className="nav-link dropdown-toggle waves-effect waves-dark" href="#!" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i className="mdi mdi-bell font-24"></i></a>
                <div className="dropdown-menu dropdown-menu-right mailbox animated bounceInDown" aria-labelledby="2" style={{"background": COLOR.light_background}}>
                    <ul className="list-style-none" >
                        <li>
                            <div className="">
                                {notificationList.map( x => {
                                    return <NotificationMessage key={x.accountNotificationId} notiInfo={x}/>
                                })}
                            </div>
                        </li>
                    </ul>
                </div>
            </li>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (Notification);
