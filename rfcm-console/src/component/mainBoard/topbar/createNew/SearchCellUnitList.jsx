import React, { useState } from "react";
import { connect } from "react-redux";
import { HTTP, MediaType, FETCH_STATE} from "../../../../util/Const";
import errorCodeToAlertCreater from "../../../../util/ErrorCodeToAlertCreater";

const SearchCellUnitList = ( { appInfo, cellInfo, isAssign } ) => {

    const [requestFlg, setRequestFlg] = useState(false);

    const onClickRequestToJoinBtn = (e) => {
        e.preventDefault();
        
        const cellId = cellInfo.cellId;
        const currentAccountId = appInfo.userInfo.currentUserId;
        const JWT_TOKEN = appInfo.appInfo.jwtToken;

        //const modalClose = document.getElementById("modalClose");
        
        //s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + `/api/cells/${cellId}/cellRequests/accounts/${currentAccountId}`, {
            method: HTTP.POST,
            headers: {
                'Content-type': MediaType.JSON,
                'Accept': MediaType.HAL_JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
            },
        }).then(res => {
            if(res.ok){        
                alert("Request successflly");
                setRequestFlg(true);
                throw(FETCH_STATE.FINE);
            }else {
                return res.json();
            }
        }).then(json => {
            try{
                errorCodeToAlertCreater(json);
            }catch(error){
                throw error;
            }
        }).catch(error => {
            if(!error === FETCH_STATE.FINE){
                console.error(error);
                alert("Client unexpect error.");
            }
        });
        //e: Ajax ----------------------------------
    }
    return (
        <>
            {/* <div>{cellInfo.cellName}</div> */}

            {/* <!-- CellList --> */}
            <div className="d-flex flex-row comment-row m-t-0">
                <div className="p-2"><img src="./public/assets/images/users/1.jpg" alt="user" width="50" className="rounded-circle"/></div>
                <div className="comment-text w-100">
                    <h6 className="font-medium">{cellInfo.cellName}</h6>
                    <span className="m-b-15 d-block">{cellInfo.cellDescription} </span>
                    <div className="comment-footer">
                        <span className="text-muted float-right">{cellInfo.createDate}</span>
                        {!isAssign && !requestFlg && <button type="button" className="btn btn-success btn-sm" onClick={onClickRequestToJoinBtn}>Request to join</button>}
                        {!isAssign && requestFlg && <span className="text-muted float-left">Requested</span>}
                        {isAssign && <span className="text-muted float-left">Already joined this Cell Unit</span>}
                    </div>
                </div>
            </div>

            <hr/>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (SearchCellUnitList);
