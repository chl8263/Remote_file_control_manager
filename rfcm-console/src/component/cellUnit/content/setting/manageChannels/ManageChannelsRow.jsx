import React, { useState } from "react";
import { connect } from "react-redux";

import { HTTP, MediaType, FETCH_STATE} from "../../../../../util/Const";
import errorCodeToAlertCreater from "../../../../../util/ErrorCodeToAlertCreater";

const ACTIVE = 1;
const INACTIVE = 0;

const ManageChannelsRow = ( {appInfo, channelInfo, getChannelList } ) => {

    const [channelName, setChannelName] = useState(channelInfo.channelName);
    const [status, setStatus] = useState(channelInfo.active);

    const onclickAvtive = () => {
        updateAcitiveState(1);
    };

    const onclickInavtive = () => {
        updateAcitiveState(0);
    };

    const updateAcitiveState = (state) => {
        if(confirm(`Would you like to Active ${channelName}?`)){
            const JWT_TOKEN = appInfo.appInfo.jwtToken;
            const channelId = channelInfo.channelId;

            //s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + `/api/channels/${channelId}/active`, {
                method: HTTP.PATCH,
                headers: {
                    'Content-type': MediaType.JSON,
                    'Accept': MediaType.HAL_JSON,
                    'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
                },
                body: state
            }).then((res) => {
                if(!res.ok && res.status !== HTTP.STATUS_CREATED && res.status !== HTTP.STATUS_BAD_REQUEST){
                    throw res;
                }
                return res;
            }).then((res) => {
                if(res.ok){        
                    alert("update successfully");
                    getChannelList();
                    throw(FETCH_STATE.FINE);
                }else {
                    return res.json();
                }
            }).then((res) => {
                try{
                    errorCodeToAlertCreater(res);
                }catch(error){
                    throw error;
                }
            }).catch(error => {
                if(!error === FETCH_STATE.FINE){
                    console.error(error);
                    alert("Client unexpect error.");
                }
            });
            // e: Ajax ----------------------------------
        }
    };

    return (
        <>
            <tr>
                <td>{channelName}</td>
                {(status === ACTIVE) && 
                    <>
                        <td className="text-success">Active</td> 
                        <td>
                            <a onClick={onclickInavtive} href="javascript:void(0)" data-toggle="tooltip" data-placement="top" title="Reject">
                                <i className="mdi mdi-close"></i>
                            </a>
                        </td>
                    </>
                }
                {(status === INACTIVE) && 
                    <>
                        <td class="text-danger">Inactive</td> 
                        <td>
                            <a onClick={onclickAvtive} href="javascript:void(0)" data-toggle="tooltip" data-placement="top" title="Accept">
                            <i className="mdi mdi-check"></i>
                            </a>
                        </td>
                    </>
                }
            </tr>
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (ManageChannelsRow);