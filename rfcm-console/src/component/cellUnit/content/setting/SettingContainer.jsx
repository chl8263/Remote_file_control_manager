import React, { useEffect } from "react";
import $ from "jquery";

import PreLoader from "../../../PreLoader";
import RequestCell from "../setting/requestCell/RequestCell";
import ManageChannels from "../setting/manageChannels/ManageChannels";
import ManageUsers from "../setting/manageUsers/ManageUsers";


const SettingContainer = () => {

    useEffect(() => {
        $(".preloader").fadeOut(); // Remove preloader.
    }, []);

    return (
        <>
            <PreLoader />

            <div className="page-breadcrumb">
                <div className="row">
                    <div className="col-12 d-flex no-block align-items-center">
                        <h4 className="page-title">Setting</h4>
                        
                    </div>
                </div>
            </div>

            <div class="container-fluid" >
                <div className="card" style={{"height" : "80vh"}}>
                    {/* <!-- Nav tabs --> */}
                    <ul className="nav nav-tabs" role="tablist">
                        <li className="nav-item"> <a className="nav-link active" data-toggle="tab" href="#requestAccounts" role="tab"><span className="hidden-sm-up"></span> <span className="hidden-xs-down">Requested Accounts</span></a> </li>
                        <li className="nav-item"> <a className="nav-link" data-toggle="tab" href="#manageChannels" role="tab"><span className="hidden-sm-up"></span> <span className="hidden-xs-down">Manage Channels</span></a> </li>
                        <li className="nav-item"> <a className="nav-link" data-toggle="tab" href="#messages" role="tab"><span className="hidden-sm-up"></span> <span className="hidden-xs-down">Manage Users</span></a> </li>
                    </ul>
                    {/* <!-- Tab panes --> */}
                    <div className="tab-content tabcontent-border">
                        <div className="tab-pane active" id="requestAccounts" role="tabpanel">
                            <div className="p-20">
                                <RequestCell />
                            </div>
                        </div>
                        <div className="tab-pane  p-20" id="manageChannels" role="tabpanel">
                            <div className="p-20">
                                <ManageChannels />
                            </div>
                        </div>
                        <div className="tab-pane p-20" id="messages" role="tabpanel">
                            <div className="p-20">
                                <ManageUsers />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SettingContainer;