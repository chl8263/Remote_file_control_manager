import React from "react"

const CellUnitTopBar = () => {
    return (
        <>
            <div className="navbar-collapse collapse" id="navbarSupportedContent" data-navbarbg="skin5">
                {/* <!-- ============================================================== -->
                <!-- toggle and nav items -->
                <!-- ============================================================== --> */}
                <ul className="navbar-nav float-left mr-auto">
                    
                    <li className="nav-item search-box">
                        <input type="text" className="form-control" placeholder="Search Cell" style={{"marginLeft": "15px", width: 300}}/>
                    </li>
                </ul>
                {/* <!-- ============================================================== -->
                <!-- Right side toggle and nav items -->
                <!-- ============================================================== --> */}
                <ul className="navbar-nav float-right">
                    {/* <!-- ============================================================== -->
                    <!-- Comment -->
                    <!-- ============================================================== --> */}
                    <li className="nav-item dropdown">
                        <a className="nav-link dropdown-toggle waves-effect waves-dark" href="" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i className="mdi mdi-bell font-24"></i>
                        </a>
                        <div className="dropdown-menu" aria-labelledby="navbarDropdown">
                            <a className="dropdown-item" href="#">Action</a>
                            <a className="dropdown-item" href="#">Another action</a>
                            <div className="dropdown-divider"></div>
                            <a className="dropdown-item" href="#">Something else here</a>
                        </div>
                    </li>
                    {/* <!-- ============================================================== -->
                    <!-- End Comment -->
                    <!-- ============================================================== -->
                    <!-- ============================================================== -->
                    <!-- Messages -->
                    <!-- ============================================================== --> */}
                    <li className="nav-item dropdown">
                        <a className="nav-link dropdown-toggle waves-effect waves-dark" href="" id="2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i className="font-24 mdi mdi-comment-processing"></i>
                        </a>
                        <div className="dropdown-menu dropdown-menu-right mailbox animated bounceInDown" aria-labelledby="2">
                            <ul className="list-style-none">
                                <li>
                                    <div className="">
                                        {/* <!-- Message --> */}
                                        <a href="#!" className="link border-top">
                                            <div className="d-flex no-block align-items-center p-10">
                                                <span className="btn btn-success btn-circle"><i className="ti-calendar"></i></span>
                                                <div className="m-l-10">
                                                    <h5 className="m-b-0">Event today</h5> 
                                                    <span className="mail-desc">Just a reminder that event</span> 
                                                </div>
                                            </div>
                                        </a>
                                        {/* <!-- Message --> */}
                                        <a href="#!" className="link border-top">
                                            <div className="d-flex no-block align-items-center p-10">
                                                <span className="btn btn-info btn-circle"><i className="ti-settings"></i></span>
                                                <div className="m-l-10">
                                                    <h5 className="m-b-0">Settings</h5> 
                                                    <span className="mail-desc">You can customize this template</span> 
                                                </div>
                                            </div>
                                        </a>
                                        {/* <!-- Message --> */}
                                        <a href="#!" className="link border-top">
                                            <div className="d-flex no-block align-items-center p-10">
                                                <span className="btn btn-primary btn-circle"><i className="ti-user"></i></span>
                                                <div className="m-l-10">
                                                    <h5 className="m-b-0">Pavan kumar</h5> 
                                                    <span className="mail-desc">Just see the my admin!</span> 
                                                </div>
                                            </div>
                                        </a>
                                        {/* <!-- Message --> */}
                                        <a href="#!" className="link border-top">
                                            <div className="d-flex no-block align-items-center p-10">
                                                <span className="btn btn-danger btn-circle"><i className="fa fa-link"></i></span>
                                                <div className="m-l-10">
                                                    <h5 className="m-b-0">Luanch Admin</h5> 
                                                    <span className="mail-desc">Just see the my new admin!</span> 
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </li>
                    {/* <!-- ============================================================== -->
                    <!-- End Messages -->
                    <!-- ============================================================== --> */}

                    {/* <!-- ============================================================== -->
                    <!-- User profile and search -->
                    <!-- ============================================================== --> */}
                    <li className="nav-item dropdown">
                        <a className="nav-link dropdown-toggle text-muted waves-effect waves-dark pro-pic" href="" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><img src="./public/assets/images/users/1.jpg" style={{"marginTop": "15px"}}  alt="user" className="rounded-circle" width="31"/></a>
                        <div className="dropdown-menu dropdown-menu-right user-dd animated">
                            <a className="dropdown-item" href="#!"><i className="ti-user m-r-5 m-l-5"></i> My Profile</a>
                            <a className="dropdown-item" href="#!"><i className="ti-wallet m-r-5 m-l-5"></i> My Balance</a>
                            <a className="dropdown-item" href="#!"><i className="ti-email m-r-5 m-l-5"></i> Inbox</a>
                            <div className="dropdown-divider"></div>
                            <a className="dropdown-item" href="#!"><i className="ti-settings m-r-5 m-l-5"></i> Account Setting</a>
                            <div className="dropdown-divider"></div>
                            <form id="logoutForm" >
                                <button className="dropdown-item" type="submit"><i className="fa fa-power-off m-r-5 m-l-5"></i> Logout</button>
                            </form>
                        </div>
                    </li>
                    {/* <!-- ============================================================== -->
                    <!-- User profile and search -->
                    <!-- ============================================================== --> */}
                </ul>
            </div>
        </>
    );
}

export default CellUnitTopBar;