import { createAction, createReducer, configureStore } from '@reduxjs/toolkit';
import { PAGE_ROUTE } from "./util/Const"

const switchMainPageRoute = createAction("SWITCHMAINPAGEROUTE");
const addJwtToken = createAction("ADDJWTTOKEN");
const addUserInfo = createAction("ADDUSERINFO");
const renewCellInfo = createAction("RENEWCELLINFO");
// const addUserName = createAction("ADDUSERNAME");
// const addUserId = createAction("ADDUSERNAME");


const reducer = createReducer(
    {
        mainPageRoute: PAGE_ROUTE.LODING,
        appInfo: {
            jwtToken: "",
        },
        userInfo: {
            currentUserId: "",
            currentUserName: "",
            currentUserRole: ""
        },
        cellInfo: {
            cellId: 0,
            cellName: "",
            role: "",
        },
    },
    {
        [switchMainPageRoute]: (state, action) => {
            return { ...state, mainPageRoute: action.payload };
        },
        [addJwtToken]: (state, action) => {
            return { ...state, 
                appInfo: {
                    ...state.appInfo,
                    jwtToken: action.payload,
                },
            };
        },
        [addUserInfo]: (state, action) => {
            return { ...state, 
                userInfo: {
                    ...state.userInfo,
                    currentUserId: action.payload.accountId,
                    currentUserName: action.payload.accountName,
                    currentUserRole: action.payload.role,
                }
                // appInfo: {
                //     ...state.appInfo,
                //     currentUserId: action.payload.accountId,
                //     currentUserName: action.payload.accountName,
                //     currentUserRole: action.payload.role,
                // },
            };
        },
        [renewCellInfo]: (state, action) => {
            return { ...state, 
                cellInfo: {
                    cellId: action.payload.cellId,
                    cellName: action.payload.cellName,
                    role: action.payload.role,
                },
            };
        },
        // [addUserName]: (state, action) => {
        //     return { ...state, 
        //         appInfo: {
        //             ...state.appInfo,
        //             currentUserName: action.payload,
        //         },
        //     };
        // },
    }
);

const store = configureStore({reducer});

export const actionCreators = {
    switchMainPageRoute,
    addJwtToken,
    addUserInfo,
    renewCellInfo,
    //addUserName,
}

export default store;