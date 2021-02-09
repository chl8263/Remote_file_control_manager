import React, { useEffect } from "react";
import $ from "jquery";
import { connect } from "react-redux";
import { HashRouter, Route, Link } from "react-router-dom";

import { actionCreators } from "../store";
import { PAGE_ROUTE, HTTP, MediaType, ROLE, COLOR} from "../util/Const";

import PreLoader from "../component/PreLoader";
import CellUnitTopBar from "../component/cellUnit/frame/CellUnitTopBar";
import CellUnitSide from "../component/cellUnit/frame/side/CellUnitSide";
import ContentMatcher from "../component/cellUnit/ContentMatcher";


const CellUnit = ({ switchMainBoard, appInfo }) => {

    useEffect(() => {
        history.pushState('','', `/cell-unit/${appInfo.cellInfo.cellId}`);
        $(".preloader").fadeOut(); // Remove preloader.
    }, []);

    const onClickReturnMainBoard = () => {
        switchMainBoard();
    }

    return(
        <div id="main-wrapper">

            <PreLoader />

            <header className="topbar" data-navbarbg="skin5">
                <nav className="navbar top-navbar navbar-expand-md navbar-dark">
                    <div className="navbar-header" data-logobg="skin5">
                        {/* <!-- This is for the sidebar toggle which is visible on mobile only --> */}
                        {/* <a className="nav-toggler waves-effect waves-light d-block d-md-none" href="#!"><i className="ti-menu ti-close"></i></a> */}
                        {/* <!-- ============================================================== -->
                        <!-- Logo -->
                        <!-- ============================================================== --> */}
                        <a className="navbar-brand" >
                            {/* <!-- Logo icon --> */}
                            <b className="logo-icon p-l-10">
                                <img onClick={onClickReturnMainBoard} style={{"cursor": "pointer"}} src="./public/assets/images/returnArrow.png" alt="homepage" className="light-logo" />
                            </b>
                            {/* <!--End Logo icon -->
                            <!-- Logo text --> */}
                            <span className="logo-text" style={{"marginLeft": "40px"}}>
                                {/* <!-- dark Logo text --> */}
                                <div  style={{"color": COLOR.light_background}}>{appInfo.cellInfo.cellName}</div>
                            </span>
                            {/* <!-- Logo icon --> */}
                            {/* <!-- <b className="logo-icon"> -->
                                <!--You can put here icon as well // <i className="wi wi-sunset"></i> //-->
                                <!-- Dark Logo icon -->
                                <!-- <img src="../../assets/images/logo-text.png" alt="homepage" className="light-logo" /> -->
                            <!-- </b> -->
                            <!--End Logo icon --> */}
                        </a>
                        {/* <!-- ============================================================== -->
                        <!-- End Logo -->
                        <!-- ============================================================== -->
                        <!-- ============================================================== -->
                        <!-- Toggle which is visible on mobile only -->
                        <!-- ============================================================== --> */}
                        <a className="topbartoggler d-block d-md-none waves-effect waves-light" href="#!" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"><i className="ti-more"></i></a>
                    </div>
                    {/* <!-- ============================================================== -->
                    <!-- End Logo -->
                    <!-- ============================================================== --> */}
                    <CellUnitTopBar />
                </nav>
            </header>
            {/* <!-- ============================================================== -->
            <!-- End Topbar header -->
            <!-- ============================================================== -->*/}

            {/* <!-- ============================================================== -->
            <!-- Left Sidebar - style you can find in sidebar.scss  -->
            <!-- ============================================================== -->  */}
            <HashRouter>
                <CellUnitSide />
                
                {/* <!-- ============================================================== -->
                <!-- End Left Sidebar - style you can find in sidebar.scss  -->
                <!-- ============================================================== --> */}

                {/* <!-- ============================================================== -->
                <!-- Main content  -->
                <!-- ============================================================== --> */}
                <div className="page-wrapper">

                    <Route path="/:name" render={(props) => <ContentMatcher {...props} />} />
                    {/* <div className="page-breadcrumb">
                        <div className="row">
                            <div className="col-12 d-flex no-block align-items-center">
                                <h4 className="page-title">Tables</h4>
                                <div className="ml-auto text-right">
                                    <nav aria-label="breadcrumb">
                                        <ol className="breadcrumb">
                                            <li className="breadcrumb-item"><a href="#">Home</a></li>
                                            <li className="breadcrumb-item active" aria-current="page">Library</li>
                                        </ol>
                                    </nav>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="container-fluid">
                    </div> */}
                </div>
                {/* <!-- ============================================================== -->
                <!-- Main content  -->
                <!-- ============================================================== --> */}

            </HashRouter>
        </div>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

const mapDispathToProps = (dispatch) => {
    return {
        switchMainBoard: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.MAINBOARD)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (CellUnit);