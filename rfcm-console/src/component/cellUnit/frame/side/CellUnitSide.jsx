import React, { useEffect } from "react"
import { HashRouter, Route, Link } from "react-router-dom";

import { CellUnitRoles } from "../../../../util/Const";

import SideChannelContainer from "../side/channel/SideChannelContainer";
import SideSetting from "../../frame/side/setting/SideSetting";
import { connect } from "react-redux";

const CellUnitSide = ({ appInfo }) => {

    useEffect(() => {
        
    }, []);

    return (
        <>
            <aside className="left-sidebar" data-sidebarbg="skin5">
                {/* <!-- Sidebar scroll--> */}
                <div className="scroll-sidebar doScroll scrollable" style={{"height": "98vh"}}>
                    {/* <!-- Sidebar navigation--> */}
                    <nav className="sidebar-nav">
                        <ul id="sidebarnav" className="p-t-30">

                            {/* Common of CellUnit */}

                            <Link to="/calendar"><li className="sidebar-item"> <a className="sidebar-link waves-effect waves-dark sidebar-link" href="#!" aria-expanded="false"><i className="mdi mdi-calendar-check"></i><span className="hide-menu">Calendar</span></a></li></Link>
                            <Link to="/charts"><li className="sidebar-item"> <a className="sidebar-link waves-effect waves-dark sidebar-link" href="#!"  aria-expanded="false"><i className="mdi mdi-chart-bar"></i><span className="hide-menu">Charts</span></a></li></Link>
                            <Link to="/gallery"><li className="sidebar-item"> <a className="sidebar-link waves-effect waves-dark sidebar-link" href="#!"  aria-expanded="false"><i className="mdi mdi-chart-bubble"></i><span className="hide-menu">Gallery</span></a></li></Link>

                            <hr className="hr1"/>

                            {/* Channel */}
                            <SideChannelContainer />        
                            

                            {/* Direct Message */}
                            <li className="sidebar-item sideContainer"> <a className="sidebar-link has-arrow waves-effect waves-dark" href="#!"  aria-expanded="false"><span className="hide-menu">Direct Message</span></a>
                                <ul aria-expanded="false" className="collapse  first-level">
                                    <li className="sidebar-item"><a href="#!"  className="sidebar-link"><i className="mdi mdi-note-outline"></i><span className="hide-menu"> Ewan </span></a></li>
                                    <li className="sidebar-item"><a href="#!"  className="sidebar-link"><i className="mdi mdi-note-plus"></i><span className="hide-menu"> Ewan2 </span></a></li>
                                </ul>
                            </li>
                            
                            {/* Setting for page admin */}
                            {(appInfo.cellInfo.role === CellUnitRoles.CREATOR || appInfo.cellInfo.role === CellUnitRoles.ADMIN)
                                && <SideSetting /> }
                        </ul>
                    </nav>
                    {/* <!-- End Sidebar navigation --> */}
                </div>
                {/* <!-- End Sidebar scroll--> */}
            </aside>
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

const mapDispathToProps = (dispatch) => {
    return {
    };
}

export default connect(mapStateToProps, mapDispathToProps) (CellUnitSide);