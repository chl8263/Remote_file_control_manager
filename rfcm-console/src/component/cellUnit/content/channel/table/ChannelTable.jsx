import React, { useEffect } from "react";

import ChannelTableTr from "../table/ChannelTableTr";

const ChannelTable = ( { channelPostList, updateChannelPostId, channelId } ) => {

    return(
        <>
            <table id="zero_config" className="table table-striped table-bordered dataTable" role="grid" aria-describedby="zero_config_info">
                <thead>
                    <tr role="row">
                        <th className="sorting_asc" tabIndex="0" aria-controls="zero_config" rowSpan="1" colSpan="1" aria-sort="ascending" aria-label="Name: activate to sort column descending" style={{"width": "120px"}}>Number</th>
                        <th className="sorting" tabIndex="0" aria-controls="zero_config" rowSpan="1" colSpan="1" aria-label="Position: activate to sort column ascending" style={{"width": "400"}}>Subject</th>
                        <th className="sorting" tabIndex="0" aria-controls="zero_config" rowSpan="1" colSpan="1" aria-label="Office: activate to sort column ascending" style={{"width": "151px"}}>Writer</th>
                        <th className="sorting" tabIndex="0" aria-controls="zero_config" rowSpan="1" colSpan="1" aria-label="Start date: activate to sort column ascending" style={{"width": "200px"}}>Time</th>
                        <th className="sorting" tabIndex="0" aria-controls="zero_config" rowSpan="1" colSpan="1" aria-label="Salary: activate to sort column ascending" style={{"width": "120px"}}>View</th>
                    </tr>
                </thead>
                <tbody>
                    {channelPostList.map( x => {
                        return <ChannelTableTr key={x.channelPostId} channelPostInfo={x} updateChannelPostId={updateChannelPostId} channelId={channelId}/>
                    })}
                </tbody>
            </table>
        </>
    );
};

export default ChannelTable;