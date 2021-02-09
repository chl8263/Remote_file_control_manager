import React, { useEffect, useState } from "react";

import { HTTP, MediaType, FETCH_STATE } from "../../../../../util/Const";
import { connect } from "react-redux";

const ChannelTableTr = ( { appInfo, channelPostInfo, updateChannelPostId, channelId } ) => {

    const [viewCount, setViewCount] = useState(channelPostInfo.viewCount);

    const onClickTr = () => {
        updateChannelPostId(channelPostInfo.channelPostId);
        
        if(appInfo.userInfo.currentUserId !== channelPostInfo.accountId){
            setViewCount(viewCount + 1);
            updateViewCount(viewCount + 1);
        }
    };

    const updateViewCount = (viewCount) => {
        const JWT_TOKEN = appInfo.appInfo.jwtToken;
        const channelPostId = channelPostInfo.channelPostId;

        //s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + `/api/channels/${channelId}/channelPosts/${channelPostId}/viewCount`, {
            method: HTTP.PATCH,
            headers: {
                'Content-type': MediaType.JSON,
                'Accept': MediaType.HAL_JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
            },
            body: viewCount
        }).then((res) => {
            if(!res.ok && res.status !== HTTP.STATUS_CREATED && res.status !== HTTP.STATUS_BAD_REQUEST){
                throw res;
            }
            return res;
        }).then((res) => {
            if(res.ok){        
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
            <tr onClick={onClickTr} data-toggle="modal" data-target="#channelContent" role="row" className="odd">
                <td className="sorting_1">{channelPostInfo.channelPostId}</td>
                <td>{channelPostInfo.channelPostName}</td>
                <td>{channelPostInfo.accountName}</td>
                <td>{channelPostInfo.createDate}</td>
                <td>{viewCount}</td>
            </tr>
        </>
    );
};


const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (ChannelTableTr);