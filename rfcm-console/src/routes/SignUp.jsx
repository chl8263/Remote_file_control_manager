import React, { useState, useEffect } from "react";
import { connect } from "react-redux";
import $ from "jquery";

import PreLoader from "../component/PreLoader";

import { actionCreators } from "../store";
import { PAGE_ROUTE, HTTP, MediaType, FETCH_STATE} from "../util/Const";
import errorCodeToAlertCreater from "../util/ErrorCodeToAlertCreater";

const SignUp = ( {switchLogin} ) => {

    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    useEffect(() => {
        history.pushState('','', '/SignUp');
        $(".preloader").fadeOut();
    }, []);

    const onChangeUserNameInput = (e) => {
        setUserName(e.target.value);
    };

    const onChangePasswordInput = (e) => {
        setPassword(e.target.value);
    };

    const onChangeConfirmPasswordInput = (e) => {
        setConfirmPassword(e.target.value);
    };

    const onSubmitSignUpForm = (e) => {
        e.preventDefault();
        if(password !== confirmPassword){
            alert("Not match password, Please check again.");
        }else {
            const accountInfo = {
                accountname: userName,
                password: password
            }
            // s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + "/api/accounts", {
                method: HTTP.POST,
                headers: {
                    'Content-type': MediaType.JSON,
                    'Accept': MediaType.HAL_JSON
                },
                body: JSON.stringify(accountInfo)
            }).then((res) => {
                if(res.ok){
                    alert("Create account successfully");
                    switchLogin();     
                    throw(FETCH_STATE.FINE);
                }else {
                    return res.json();
                }
            }).then((json) => {
                try{
                    errorCodeToAlertCreater(json);
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
        }
    };

    const onClickBackToLoigin = () => {
        switchLogin();
    };

    return (
        <div className="main-wrapper">

            <PreLoader />

            <div className="auth-wrapper d-flex no-block justify-content-center align-items-center bg-dark">
                <div className="auth-box bg-dark border-top border-secondary">
                    <div>
                        <div className="text-center p-t-20 p-b-20">
                            <h2> Cellit SignUp </h2>
                        </div>
                        {/* <!-- Form --> */}
                        <form className="form-horizontal m-t-20" onSubmit={onSubmitSignUpForm}>
                            <div className="row p-b-30">
                                <div className="col-12">
                                    <div className="input-group mb-3">
                                        <div className="input-group-prepend">
                                            <span className="input-group-text bg-success text-white" id="basic-addon1"><i className="ti-user"></i></span>
                                        </div>
                                        <input type="text" className="form-control form-control-lg" onChange={onChangeUserNameInput} value={userName} placeholder="Username" aria-label="Username" aria-describedby="basic-addon1" required/>
                                    </div>
                                    {/* <!-- email --> */}
                                    {/* <div className="input-group mb-3">
                                        <div className="input-group-prepend">
                                            <span className="input-group-text bg-danger text-white" id="basic-addon1"><i className="ti-email"></i></span>
                                        </div>
                                        <input type="text" className="form-control form-control-lg" placeholder="Email Address" aria-label="Username" aria-describedby="basic-addon1" required/>
                                    </div> */}
                                    <div className="input-group mb-3">
                                        <div className="input-group-prepend">
                                            <span className="input-group-text bg-warning text-white" id="basic-addon2"><i className="ti-pencil"></i></span>
                                        </div>
                                        <input type="password" className="form-control form-control-lg" onChange={onChangePasswordInput} value={password} placeholder="Password" aria-label="Password" aria-describedby="basic-addon1" required/>
                                    </div>
                                    <div className="input-group mb-3">
                                        <div className="input-group-prepend">
                                            <span className="input-group-text bg-info text-white" id="basic-addon2"><i className="ti-pencil"></i></span>
                                        </div>
                                        <input type="password" className="form-control form-control-lg" onChange={onChangeConfirmPasswordInput} value={confirmPassword} placeholder=" Confirm Password" aria-label="Password" aria-describedby="basic-addon1" required/>
                                    </div>
                                </div>
                            </div>
                            <div className="row border-top border-secondary">
                                <div className="col-12">
                                    <div className="form-group">
                                        <div className="p-t-20">
                                            <button className="btn btn-block btn-lg btn-info" type="submit">Sign Up</button>
                                            <br/>
                                            <button className="btn btn-success float-right" type="button" onClick={onClickBackToLoigin}>Back to Login</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}

const mapStateToProps = (state, ownProps) => {
    return state;
}

const mapDispathToProps = (dispatch) => {
    return {
        switchLogin: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.LOGIN)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (SignUp);
