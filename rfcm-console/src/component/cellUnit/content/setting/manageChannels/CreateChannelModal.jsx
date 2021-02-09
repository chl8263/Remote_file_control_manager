import React, { useEffect, useRef, useState } from "react";

import { FETCH_STATE ,PAGE_ROUTE, HTTP, MediaType} from "../../../../../util/Const";
import errorCodeToAlertCreater from "../../../../../util/ErrorCodeToAlertCreater";
import { connect } from "react-redux";

const CreateChannelModal = ( { appInfo, getChannelList } ) => {

    const currentCellId = appInfo.cellInfo.cellId;
    const JWT_TOKEN = appInfo.appInfo.jwtToken;

    const modalClose = document.getElementById("createChannelModalClose");

    const [createChannelName, setCreateChannelName] = useState("");

    useEffect(() => {
       
    }, []);

    const onChangeChannelName = (e) => {
        setCreateChannelName(e.target.value);
    };

    const onKeyPressChannelName = (e) => {
        if(e.key == 'Enter'){
            onClickCreateChannelSave();
        }
    };

    const onClickCreateChannelSave = () => {

        const channelInfo = {
            cellId: currentCellId,
            channelName: createChannelName,
        };

        //s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + `/api/channels`, {
            method: HTTP.POST,
            headers: {
                'Content-type': MediaType.JSON,
                'Accept': MediaType.HAL_JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
            },
            body: JSON.stringify(channelInfo)
        }).then((res) => {
            if(!res.ok && res.status !== HTTP.STATUS_CREATED && res.status !== HTTP.STATUS_BAD_REQUEST){
                throw res;
            }
            return res;
        }).then((res) => {
            if(res.ok){        
                alert("Create Channel successfully.");
                modalClose.click();
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
    };

    return (
        <>
            <div className="modal fade none-border" id="createChannelModal">
                <div className="modal-dialog" style={{"width": "500px"}}>
                    <div className="modal-content">
                        <div className="modal-header">
                            <h4 className="modal-title"><strong>Create new Channel</strong></h4>
                            <button id="createChannelModalClose" type="button" className="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>
                        <div className="modal-body">
                                <div className="row">
                                    <div className="col-md-6">
                                        <label className="control-label">Channel Name</label>
                                        <input onKeyPress={onKeyPressChannelName} className="form-control form-white" onChange={onChangeChannelName} value={createChannelName} placeholder="Enter Channel name" type="text"  />
                                    </div>
                                </div>
                            
                        </div>
                        <div className="modal-footer">
                            <button type="button" onClick={onClickCreateChannelSave} className="btn btn-success waves-effect waves-light save-category">Save</button>
                            <button type="button" className="btn btn-danger waves-effect" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (CreateChannelModal);