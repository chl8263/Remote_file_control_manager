import React, { useEffect, useRef, useState } from "react";

import { FETCH_STATE ,PAGE_ROUTE, HTTP, MediaType} from "../../../../util/Const";
import errorCodeToAlertCreater from "../../../../util/ErrorCodeToAlertCreater";
import { connect } from "react-redux";

const CreateChannelPostModal = ( { appInfo, channelData, getCahnnelPostListByPageNumber } ) => {

    const modalClose = document.getElementById("modalClose");

    const [subject, setSubject] = useState("");
    const [content, setContent] = useState("");

    useEffect(() => {
        var quill = new Quill('#editor', {
            theme: 'snow'
        });
    }, []);

    const onChangeSubject = (e) => {
        setSubject(e.target.value);
        
    };

    const onClickSave = () => {
        if(confirm("Do you want to create new post?")){
            const content = document.getElementsByClassName("ql-editor")[0].innerHTML;

            const JWT_TOKEN = appInfo.appInfo.jwtToken;
            const userId = appInfo.userInfo.currentUserId;
            const userName = appInfo.userInfo.currentUserName;
            const channelId = channelData.channelId;
            
            const channelPostInfo = {
                channelPostName: subject,
                channelPostContent: content,
                accountId: userId,
                accountName: userName,
            }

            //s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + `/api/channels/${channelId}/channelPosts`, {
                method: HTTP.POST,
                headers: {
                    'Content-type': MediaType.JSON,
                    'Accept': MediaType.HAL_JSON,
                    'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
                },
                body: JSON.stringify(channelPostInfo)
            }).then((res) => {
                if(!res.ok && res.status !== HTTP.STATUS_CREATED && res.status !== HTTP.STATUS_BAD_REQUEST){
                    throw res;
                }
                return res;
            }).then((res) => {
                if(res.ok){        
                    alert("Create post successfully");
                    modalClose.click();
                    getCahnnelPostListByPageNumber(0);
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
            <div className="modal fade none-border" id="createChannelPost">
                <div className="modal-dialog" style={{"width": "1000px"}}>
                    <div className="modal-content">

                        <div className="modal-header">
                            <h4 className="modal-title"><strong>Create new post</strong></h4>
                            <button id="modalClose" type="button" className="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>

                        <div className="modal-body" >
                            <div className="form-group row">
                                <div className="col-md-2">
                                    <h5 className="control-label">Subject</h5>
                                </div>
                                <div className="col-md-6">
                                    <input id="cellName" name="cellName" onChange={onChangeSubject} value={subject} className="form-control form-white" placeholder="Enter subject" type="text"  />
                                </div>
                            </div>
                            <div className="modal-content" >
                                <div className="row">
                                    <div className="col-12">
                                        <div className="card">
                                            <div className="card-body">
                                                {/* <!-- Create the editor container --> */}
                                                <div id="editor" style={{"height": "60vh"}}>
                                                    {/* <p>Hello World!</p>
                                                    <p>Some initial <strong>bold</strong> text</p>
                                                    <p>
                                                        <br/>
                                                    </p> */}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>                        
                        <div className="modal-footer">
                            <button type="button" onClick={onClickSave} className="btn btn-success waves-effect">Save</button>
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

export default connect(mapStateToProps) (CreateChannelPostModal);