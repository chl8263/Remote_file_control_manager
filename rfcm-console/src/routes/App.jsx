import React, { useEffect } from "react";
import $ from "jquery";
import { connect } from "react-redux";

import { actionCreators } from "../store";

import Login from "../routes/Login";
import SignUp from "../routes/SignUp";
import MainBoard from "../routes/MainBoard";
import CellUnit from "../routes/CellUnit";
import PreLoader from "../component/PreLoader";

import { PAGE_ROUTE, HTTP, MediaType, ROLE} from "../util/Const";

import { useCookies } from 'react-cookie';


const App = ( { page, switchLogin } ) => {

    const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

    useEffect(() => {
        console.log(cookies.JWT_TOKEN);

        if(cookies.JWT_TOKEN === undefined || cookies.JWT_TOKEN === null){
            console.log("Jwt cookie 없음");
            switchLogin();
            
        }else {
            // s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + "/check", {
                method: HTTP.GET,
                headers: {
                    'Authorization': HTTP.BASIC_TOKEN_PREFIX + cookies.JWT_TOKEN
                },
                // body: JSON.stringify(accountInfo)
                
            }).then(res => {
                if(!res.ok){
                    throw res;
                }
                return res;
            }).then(res => {

                console.log(res);

            }).catch(error => {

                alert("Please check account again.");

            }).finally( () => {
                $(".preloader").fadeOut(); // Remove preloader.        
            });
            // e: Ajax ----------------------------------
        }
        
    }, []);

    useEffect(() => {
        console.log(cookies);
        console.log(cookies.name);

    }, [cookies]);

    if(page === PAGE_ROUTE.LODING){
        return (
            <>
                <PreLoader />
            </>
        )
    } else if(page === PAGE_ROUTE.LOGIN){
        console.log("sdsaddssaca");
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
    }else if(page === PAGE_ROUTE.CELLUNIT){
        return (
            <>
                <CellUnit />
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
        //addUserName: (username) => dispatch(actionCreators.addUserName(username)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (App);