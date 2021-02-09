import React from "react";
import { connect } from "react-redux";

const ChannelPostContentComment = ( { appInfo, commentInfo } ) => {

    const JWT_TOKEN = appInfo.appInfo.jwtToken;
    const currentUserId = appInfo.userInfo.currentUserId;
    const currentUserName = appInfo.userInfo.currentUserName;

    return (
        <>
            {currentUserId != commentInfo.accountId && 
                <li className="chat-item">
                    <div className="chat-img"><img src="" alt="user"/></div>
                    <div className="chat-content">
                        <h6 className="font-medium">{commentInfo.accountName}</h6>
                        <div className="box bg-light-info">{commentInfo.channelPostComment}</div>
                    </div>
                    <div className="chat-time">{commentInfo.createDate}</div>
                </li>
            }
            {currentUserId == commentInfo.accountId && 
                <li className="odd chat-item">
                    <div className="chat-content">
                        <div className="box bg-light-inverse">{commentInfo.channelPostComment}</div>
                        <div className="chat-time">{commentInfo.createDate}</div>
                        <br/>
                    </div>
                </li>
            }
           
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (ChannelPostContentComment);