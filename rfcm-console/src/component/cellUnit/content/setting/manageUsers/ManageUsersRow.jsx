import React, { useState } from "react";
import { connect } from "react-redux";

import { HTTP, MediaType, FETCH_STATE} from "../../../../../util/Const";
import errorCodeToAlertCreater from "../../../../../util/ErrorCodeToAlertCreater";

const ManageUsersRow = ( {appInfo, accountInfo, getAccountList } ) => {

    const cellId = appInfo.cellInfo.cellId;
    const [accountName, setChannelName] = useState(accountInfo.accountName);

    const removeAccountAtCell = (state) => {
        if(confirm(`Would you like to Remove ${accountName} at this cell?`)){
            const JWT_TOKEN = appInfo.appInfo.jwtToken;

            //s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + `/api/cells/${cellId}/accounts/${accountInfo.accountId}`, {
                method: HTTP.DELETE,
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
                    alert("Delete successfully.");
                    getAccountList();
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
                <td>{accountName}</td>
                <td>
                    <a onClick={removeAccountAtCell} href="javascript:void(0)" data-toggle="tooltip" data-placement="top" title="Reject">
                        <i className="mdi mdi-close"></i>
                    </a>
                </td>
            </tr>
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (ManageUsersRow);