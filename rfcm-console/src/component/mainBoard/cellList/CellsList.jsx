import React from "react"
import { connect } from "react-redux";

import { PAGE_ROUTE } from "../../../util/Const";
import { actionCreators } from "../../../store";


const CellsList = ({cellInfo, appInfo, switchCellUnit, renewCellInfo}) => {

    const onClickCellsList = () => {
        switchCellUnit();
        renewCellInfo(cellInfo);
    }

    return (
        <>
            <div className="comment-widgets">
                {/* <!-- CellList --> */}
                <div className="d-flex flex-row comment-row m-t-0" onClick={onClickCellsList}>
                    <div className="p-2"><img src="./public/assets/images/users/1.jpg" alt="user" width="50" className="rounded-circle"/></div>
                    <div className="comment-text w-100">
                        <h6 className="font-medium">{cellInfo.cellName}</h6>
                        <span className="m-b-15 d-block">{cellInfo.cellDescription} </span>
                        <div className="comment-footer">
                            <span className="text-muted float-right">{cellInfo.createDate}</span>
                            {/* <button type="button" className="btn btn-cyan btn-sm">Edit</button>
                            <button type="button" className="btn btn-success btn-sm">Publish</button>
                            <button type="button" className="btn btn-danger btn-sm">Delete</button> */}
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

const mapDispathToProps = (dispatch) => {
    return {
        switchCellUnit: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.CELLUNIT)),
        renewCellInfo: (cellInfo) => dispatch(actionCreators.renewCellInfo(cellInfo)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (CellsList);