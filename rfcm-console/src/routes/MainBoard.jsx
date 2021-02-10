import React, { useEffect, useState } from "react";
import $ from "jquery";
import { connect } from "react-redux";

import PreLoader from "../component/PreLoader";
import { PAGE_ROUTE, HTTP, MediaType} from "../util/Const";
import { actionCreators } from "../store";

import MainBoardTopbar from "../component/mainBoard/topbar/MainBoardTopbar";
import CellsListContainer from "../component/mainBoard/cellList/CellsListContainer";
import CreateCellUnitModal from "../component/mainBoard/topbar/createNew/CreateCellUnitModal";
import SearchAllCellUnitModal from "../component/mainBoard/topbar/createNew/SearchAllCellUnitModal";

const MainBoard = ( {appInfo} ) => {


    useEffect(() => {
        history.pushState('','', '/main-board');
        $(".preloader").fadeOut(); // Remove preloader.

        
    }, []);

    
    return (
        <>
            <PreLoader />

            <div>main board</div>
        </> 
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

const mapDispathToProps = (dispatch) => {
    return {
        switchLogin: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.LOGIN)),
        initJwtToken: () => dispatch(actionCreators.addJwtToken("")),
        initUserInfo: () => dispatch(actionCreators.addUserInfo("")),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (MainBoard);