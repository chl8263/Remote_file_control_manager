import React, { useEffect } from "react";
import { useCookies } from 'react-cookie';
import { connect } from "react-redux";
import $ from "jquery";

import { actionCreators } from "../store";
import { PAGE_ROUTE, HTTP, MediaType, ROLE} from "../util/Const";

import PreLoader from "../component/PreLoader";
import Login from "../routes/Login";
import SignUp from "../routes/SignUp";
import MainBoard from "../routes/MainBoard";

const App = ( { page, switchLogin, switchMainBoard } ) => {

    const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

    useEffect(() => {
        if(cookies.JWT_TOKEN === undefined || cookies.JWT_TOKEN === null || cookies.UID === undefined || cookies.UID === null){
            switchLogin();
        }else {
            // s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + "/api/accounts/check", {
                method: HTTP.GET,
                headers: {
                    'Accept': MediaType.JSON,
                    'Authorization': HTTP.BASIC_TOKEN_PREFIX + cookies.JWT_TOKEN,
                    'Uid': cookies.UID,
                },                
            }).then(res => { if(!res.ok){ throw res; } return res; })
            .then(res => { switchMainBoard(); })
            .catch(error => { switchLogin(); })
            .finally(() => { $(".preloader").fadeOut();
            });
            // e: Ajax ----------------------------------
        }
    }, []);

    useEffect(() => {
    }, [cookies]);

    if(page === PAGE_ROUTE.LODING){
        return (
            <>
                <PreLoader />
            </>
        )
    } else if(page === PAGE_ROUTE.LOGIN){
        return (
            <>
                <Login />
            </>
        )
    }else if(page === PAGE_ROUTE.SIGNUP){
        return (
            <>
                <SignUp />
            </>
        )
    }else if(page === PAGE_ROUTE.MAINBOARD){
        return (
            <>
                <MainBoard />
            </>
        )
    }else {
        return(
            <div>nothing</div>
        );
    }
};

const mapStateToProps = (state) => {
    return {page: state.mainPageRoute};
};

const mapDispathToProps = (dispatch) => {
    return {
        switchLogin: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.LOGIN)),
        switchMainBoard: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.MAINBOARD)),
        addJwtToken: (jwtToken) => dispatch(actionCreators.addJwtToken(jwtToken)),
        addUserInfo: (userInfo) => dispatch(actionCreators.addUserInfo(userInfo)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (App);