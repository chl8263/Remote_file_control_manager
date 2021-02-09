import React from "react";
import { Link } from "react-router-dom";

const SideChannelChild = ({ channelId, channelName }) => {

    const url = `/channel/${channelId}`
    return(
        <>
            <Link to={{
                pathname: url,
                channelData: {
                    channelId: channelId,
                    channelName: channelName
                }
            }}><li className="sidebar-item"><a className="sidebar-link"><i className="mdi mdi-pound"></i><span className="hide-menu"> {channelName} </span></a></li></Link>
        </>
    );
};

export default SideChannelChild;