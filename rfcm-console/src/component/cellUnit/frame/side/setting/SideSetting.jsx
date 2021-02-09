import React from "react";
import { Link } from "react-router-dom";



const SideSetting = () => {
    return (
        <>
            <hr className="hr1"/>

            <Link to="/setting"><li className="sidebar-item"><a href="#!" className="sidebar-link"><i className="mdi mdi-settings"></i><span className="hide-menu"> Setting </span></a></li></Link>
        </>
    )
};

export default SideSetting;