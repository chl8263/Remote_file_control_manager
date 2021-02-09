import React, { useState, useRef, useEffect } from "react";
import { connect } from "react-redux";

import { FETCH_STATE ,PAGE_ROUTE, HTTP, MediaType, COLOR} from "../../../../util/Const";
import errorCodeToAlertCreater from "../../../../util/ErrorCodeToAlertCreater";

const CreateCellUnitModal = ( { appInfo, getCellList } ) => {

    const [createCellname, setCreateCellname] = useState("");
    const [createCellDescription, setCreateCellDescription] = useState("");
    const cellNameInputRef = useRef(null);

    useEffect(() => {
        cellNameInputRef.current.focus();
    }, []);

    const onChangeCreateCellname = (e) => {
        setCreateCellname(e.target.value);
    };

    const onChangeCreateCellDescription = (e) => {
        setCreateCellDescription(e.target.value);
    };

    const onSubmitCreateCell = (e) => {
        e.preventDefault();

        const JWT_TOKEN = appInfo.appInfo.jwtToken;

        const cellInfo = {
            cellName: createCellname,
            cellDescription: createCellDescription,
        }
        const modalClose = document.getElementById("modalClose");
        
        //s: Ajax ----------------------------------
        fetch(HTTP.SERVER_URL + "/api/cells", {
            method: HTTP.POST,
            headers: {
                'Content-type': MediaType.JSON,
                'Accept': MediaType.HAL_JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
            },
            body: JSON.stringify(cellInfo)
        }).then((res) => {
            if(!res.ok && res.status !== HTTP.STATUS_CREATED && res.status !== HTTP.STATUS_BAD_REQUEST){
                throw res;
            }
            return res;
        }).then((res) => {
            if(res.ok){        
                alert("Create cell successfully");
                modalClose.click();
                getCellList();
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
            <div className="modal fade none-border" id="createCellUnit">
                <div className="modal-dialog" style={{"width": "500px"}}>
                    <div className="modal-content">
                        <div className="modal-header">
                            <h4 className="modal-title"><strong>Create new Cell Unit</strong></h4>
                            <button id="modalClose" type="button" className="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>
                        <form id="createNewCellUnitForm" onSubmit={onSubmitCreateCell}>
                            <div className="modal-body">
                                    <div className="row">
                                        <div className="col-md-6">
                                            <label className="control-label">Cell Unit Name</label>
                                            <input id="cellName" name="cellName" ref={cellNameInputRef} className="form-control form-white" onChange={onChangeCreateCellname} value={createCellname} placeholder="Enter name" type="text"  />
                                        </div>
                                        <div className="col-md-6">
                                            <label className="control-label">Cell Unit Description</label>
                                            <input id="cellDescription" name="cellDescription" className="form-control form-white" onChange={onChangeCreateCellDescription} value={createCellDescription} placeholder="Enter Description" type="text"  />
                                        </div>
                                        <div className="col-md-6">
                                            <br/>
                                            <label className="control-label">Category</label>
                                            <select className="form-control form-white" data-placeholder="Choose a color..." name="category-color">
                                                <option value="success">Entertainment</option>
                                                <option value="danger">Information</option>
                                            </select>
                                        </div>
                                    </div>
                                
                            </div>
                            <div className="modal-footer">
                                <button type="submit" className="btn btn-success waves-effect waves-light save-category">Save</button>
                                <button type="button" className="btn btn-danger waves-effect" data-dismiss="modal">Close</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (CreateCellUnitModal);