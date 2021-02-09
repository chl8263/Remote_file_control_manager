import React, { useEffect, useState } from "react";

import { PAGE_ROUTE, HTTP, MediaType} from "../../../../../util/Const";
import { connect } from "react-redux";

import ManageChannelsRow from "./ManageChannelsRow";
import CreateChannelModal from "./CreateChannelModal";

const ManageChannels = ({appInfo}) => {

    const cellId = appInfo.cellInfo.cellId;

    const [channelList, setChannelList] = useState([]);

    useEffect(() => {
        getChannelList();
    }, []);

    const getChannelList = () => {
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
                console.log(res);
                setChannelList([]);
                setChannelList(res._embedded.channelEntityModelList);
            }
        }).catch(error => {
            console.error(error);
            alert("Cannot load channel list");
        });
        // e: Ajax ----------------------------------
    };

    return (
        <>
            <div className="card">
                
                <table className="table">
                    <thead>
                        <tr>
                            <th scope="col">Channel</th>
                            <th scope="col">Status</th>
                            <th scope="col">Actions</th>
                            <th scope="col"> <button type="button" data-toggle="modal" data-target="#createChannelModal" className="btn btn-success waves-effect">Create Channel</button>
                            </th>
                        </tr>
                    </thead>
                    <tbody>

                        {channelList.map(x => {
                            return <ManageChannelsRow key={x.channelId} channelInfo={x} getChannelList={getChannelList}/>
                        })}
                       
                    </tbody>
                </table>
            </div>
            <CreateChannelModal getChannelList={getChannelList} />
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (ManageChannels);