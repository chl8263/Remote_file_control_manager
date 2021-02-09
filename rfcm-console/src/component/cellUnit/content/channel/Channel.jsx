import React, { useEffect, useState, useRef } from "react";
import $ from "jquery";

import PreLoader from "../../../PreLoader";
import CreateChannelPostModal from "../channel/CreateChannelPostModal";
import ChannelPostContentModal from "../channel/ChannelPostContentModal";
import ChannelTable from "../channel/table/ChannelTable";
import TableIndicator from "../channel/table/TableIndicator";

import { connect } from "react-redux";
import { HTTP, MediaType } from "../../../../util/Const";

const Channel = ( { appInfo, data } ) => {

    const channelData = data.location.channelData;
    const JWT_TOKEN = appInfo.appInfo.jwtToken;
    const channelId = channelData.channelId;

    const [channelIdAsState, setChannelIdAsState] = useState("");
    const [channelPostList, setChannelPostList] = useState([]);
    const [tableIndicatorPageInfo, setTableIndicatorPageInfo] = useState({});
    const [tableIndicatorLinkInfo, setTableIndicatorLinkInfo] = useState({});
    const [channelPostId, setChannelPostId] = useState(0);
    const elementsNumberRef = useRef(null);
    const [postNameToSearch, setPostNameToSearch] = useState("");

    useEffect(() => {
        $(".preloader").fadeOut(); // Remove preloader.
        getCahnnelPostListByPageNumber(0);
        //getCahnnelPostList(0);
    }, []);

    useEffect(() => {
        getCahnnelPostListByPageNumber(0);
        setChannelIdAsState(channelId);
    }, [channelId]);

    const updateChannelPostId = (channelPostId) => {
        setChannelPostId(channelPostId);
    };

    const onChangePostNameToSearch = (e) => {
        setPostNameToSearch(e.target.value);
    };

    const onKeyPressOnPostNameToSearch = (e) => {
        if(e.key == 'Enter'){
            onClickPostNameToSearch(0);
        }
    };

    const onClickPostNameToSearch = () => {
        getCahnnelPostListByPageNumber(0);
    };

    const onChangeElementsNumber = () => {
        getCahnnelPostListByPageNumber(0);
    };

    const getCahnnelPostListByWholeUrl = (url) => {
        getCahnnelPostList(url);
    };

    const getCahnnelPostListByPageNumber = (pageNumber) => {
        const realPageNumber = pageNumber-1;
        const url = HTTP.SERVER_URL+`/api/channels/${channelId}/channelPosts?postNameToSearch=${postNameToSearch}&page=${realPageNumber}&size=${elementsNumberRef.current.value}&sort=createDate,DESC`;
        getCahnnelPostList(url);
    };

    const getCahnnelPostList = (url) => {
        console.warn(url);
        //s: Ajax ----------------------------------
        fetch(url, {
            method: HTTP.GET,
            headers: {
                'Content-type': MediaType.JSON,
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
            }else if("_embedded" in res){
                setChannelPostList(res._embedded.channelPostEntityModelList);
                setTableIndicatorPageInfo(res.page);
                setTableIndicatorLinkInfo(res._links);
            }else {
                setChannelPostList([]);
                setTableIndicatorPageInfo({});
                setTableIndicatorLinkInfo({});
                setChannelPostId(0);
                setPostNameToSearch("");
            }
        }).catch(error => {
            console.error(error);
            alert("Client unexpect error.");
        });
        // e: Ajax ----------------------------------
    };

    
    
    return (
        <>
            <PreLoader />
            <div className="page-breadcrumb">
                <div className="row">
                    <div className="col-12 d-flex no-block align-items-center">
                        <h4 className="page-title">Channel - {channelData.channelName}</h4>
                        
                    </div>
                </div>
            </div>

            <div className="container-fluid">
                <div className="scroll-sidebar doScroll scrollable" style={{"height": "90vh"}}>
                    <div className="card">
                        <div className="card-body">
                            <div className="table-responsive">
                                <div id="zero_config_wrapper" className="dataTables_wrapper container-fluid dt-bootstrap4">
                                    <div className="row">
                                        <div className="col-sm-12 col-md-4">
                                            <div className="dataTables_length" id="zero_config_length">
                                                <label>Show  
                                                    <select onChange={onChangeElementsNumber} ref={elementsNumberRef} name="zero_config_length" aria-controls="zero_config" className="form-control form-control-sm">
                                                        <option value="10">10</option><option value="25">25</option>
                                                        <option value="50">50</option><option value="100">100</option>
                                                    </select> entries
                                                </label>
                                            </div>
                                        </div>
                                    <div className="col-sm-12 col-md-4">
                                        <div id="zero_config_filter" className="dataTables_filter">
                                            <label>Search:<input value={postNameToSearch} onKeyPress={onKeyPressOnPostNameToSearch} onChange={onChangePostNameToSearch} type="search" className="form-control form-control-sm" placeholder="" aria-controls="zero_config"/></label>
                                            <button onClick={onClickPostNameToSearch} type="button" className="btn btn-info btn-sm"  style={{"marginLeft": "15px"}}>Search</button>
                                        </div>
                                    </div>
                                    <div className="col-sm-12 col-md-4">
                                    {/* <a className="dropdown-item" href="#!" data-toggle="modal" data-target="#createChannelPost">Create Cell Unit</a> */}
                                        
                                        <button type="button" className="btn btn-primary btn-lg" data-toggle="modal" data-target="#createChannelPost" style={{"marginLeft": "15px"}}>New post</button>
                                    </div>
                                </div>
                                <div className="row">
                                    <div className="col-sm-12"></div>
                                    <ChannelTable updateChannelPostId={updateChannelPostId} channelPostList={channelPostList} channelId={channelIdAsState}/>
                                </div>
                            </div>
                                <TableIndicator getCahnnelPostListByWholeUrl={getCahnnelPostListByWholeUrl} getCahnnelPostListByPageNumber={getCahnnelPostListByPageNumber} pageInfo={tableIndicatorPageInfo} linkInfo={tableIndicatorLinkInfo}/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <CreateChannelPostModal channelData={channelData} getCahnnelPostListByPageNumber={getCahnnelPostListByPageNumber}/>
            <ChannelPostContentModal channelData={channelData} getCahnnelPostListByPageNumber={getCahnnelPostListByPageNumber} channelPostId={channelPostId}/>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (Channel);