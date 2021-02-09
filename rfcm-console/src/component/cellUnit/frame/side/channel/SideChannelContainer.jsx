import React, { useEffect, useState } from "react";
import $ from "jquery";
import { connect } from "react-redux";

import { HTTP, MediaType } from "../../../../../util/Const";

import SideChannelChild from "./SideChannelChild";

const init = () => {

    $(".sideContainer-open").children("a").addClass("active");
    $(".sideContainer-open").children("ul").addClass("in");

    $(".sideContainer").on('click', function() {
        if($(this).children("a").hasClass("active") || $(this).children("ul").hasClass("in")){
            $(this).children("a").removeClass("active");
            $(this).children("ul").removeClass("in");
        }else {
            $(this).children("a").addClass("active");
            $(this).children("ul").addClass("in");
        }
    });
}

const SideChannelContainer = ({ appInfo }) => {

    const [channels, setChannels] = useState([]);

    useEffect(() => {
        init();
        getChannelList();
    }, []);

    const getChannelList = () => {
        const cellId = appInfo.cellInfo.cellId;
        const JWT_TOKEN = appInfo.appInfo.jwtToken;

        // s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + `/api/cells/${cellId}/channels`, {
            method: HTTP.GET,
            headers: {
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
            }else{
                setChannels(res._embedded.channelEntityModelList);
            }
        }).catch(error => {
            console.error(error);
            alert("Cannot load channel list");
        });
        // e: Ajax ----------------------------------
    };

    return (
        <>
            <li onClick={getChannelList} className="sidebar-item sideContainer sideContainer-open"> <a className="sidebar-link has-arrow waves-effect waves-dark" aria-expanded="false"><span className="hide-menu">Channel </span></a>
                <ul aria-expanded="false" className="collapse first-level">
                    {channels.filter(x => x.active == 1).map(x => {
                        return <SideChannelChild channelId={x.channelId} channelName={x.channelName}/> 
                    })}
                    
                    {/* <li className="sidebar-item"><a href="form-wizard.html" className="sidebar-link"><i className="mdi mdi-pound"></i><span className="hide-menu"> Form Wizard </span></a></li> */}
                </ul>
            </li>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

const mapDispathToProps = (dispatch) => {
    return {
        //switchMainBoard: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.MAINBOARD)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (SideChannelContainer);