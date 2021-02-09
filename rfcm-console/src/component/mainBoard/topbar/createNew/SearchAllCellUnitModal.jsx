import React, { useState } from "react";
import { connect } from "react-redux";

import { PAGE_ROUTE, HTTP, MediaType} from "../../../../util/Const";
import errorCodeToAlertCreater from "../../../../util/ErrorCodeToAlertCreater";

import SearchCellUnitList from "../createNew/SearchCellUnitList";

const SearchAllCellUnitModal = ( { appInfo, currentCellList } ) => {

    const [searchCellname, setSearchCellname] = useState("");
    const [searchedCellList, setSearchedCellList] = useState([]);

    const onSearchCreateCellname = (e) => {
        setSearchCellname(e.target.value);
    };

    const onSubmitSearchCell = (e) => {
        e.preventDefault();

        const JWT_TOKEN = appInfo.appInfo.jwtToken;

        const modalClose = document.getElementById("modalClose");
        
        //s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + `/api/cells?query=cellName%3D${searchCellname}`, {
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
            }else if("_embedded" in res && res._embedded.cellEntityModelList.length > 0){
                setSearchedCellList(res._embedded.cellEntityModelList);
            }
        }).catch(error => {
            console.error(error);
            alert("Client unexpect error.");
        });
        //e: Ajax ----------------------------------
    };

    return (
        <>
            <div className="modal fade none-border" id="searchAllCellUnit">
                <div className="modal-dialog" style={{"width": "500px"}}>
                    <div className="modal-content">
                        <div className="modal-header">
                            <h4 className="modal-title"><strong>Search All Cell Unit</strong></h4>
                            <button id="modalClose" type="button" className="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>
                        
                        <div className="modal-body">
                            <form id="createNewCellUnitForm" onSubmit={onSubmitSearchCell}>
                                <div className="row">
                                    <div className="col-md-8">
                                        <label className="control-label">Cell Unit Name</label>
                                        <input style={{"display": "inline"}} id="cellName" name="cellName" className="form-control form-white" onChange={onSearchCreateCellname} value={searchCellname} placeholder="Enter cell name to search" type="text" />
                                        <br/>
                                        <button style={{"display": "inline"}} type="submit" className="btn btn-info waves-effect">Search</button>
                                    </div>
                                </div>
                            </form>
                            <hr/>
                            <label className="control-label">Result of Cell List</label>
                            <div className="doScroll scrollable" style={{"height": "50vh"}}>
                                {searchedCellList.map( x => {
                                    var isAssign = false;
                                    for(const k of currentCellList){
                                        if(k.cellId == x.cellId){
                                            isAssign = true;
                                            break;
                                        }
                                    }
                                    return <SearchCellUnitList key={x.cellId} cellInfo={x} isAssign={isAssign}/>
                                })}
                            </div>
                            
                            {/* <div className="row">
                                <div className="col-md-6">
                                    <label className="control-label">Cell Unit Name</label>
                                </div>
                            </div>
                            <div className="row">
                                <div className="col-md-6">
                                    <label className="control-label">Cell Unit Name</label>
                                </div>
                            </div> */}
                        </div>
                        <div className="modal-footer">
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

export default connect(mapStateToProps) (SearchAllCellUnitModal);