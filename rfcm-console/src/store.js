import { createAction, createReducer, configureStore } from '@reduxjs/toolkit';
import { PAGE_ROUTE } from "./util/Const"

const switchMainPageRoute = createAction("SWITCHMAINPAGEROUTE");
const addJwtToken = createAction("ADDJWTTOKEN");
const addUserInfo = createAction("ADDUSERINFO");
const renewCellInfo = createAction("RENEWCELLINFO");
const renewFileViewInfo = createAction("RENEWFILEVIEWINFO");
const switchModalState = createAction("SWITCHMODALSTATE");
const renewCopyItem = createAction("RENEWCOPYITEM");
const renewConnections = createAction("RENEWCONNECTIONS");

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
        fileViewInfo: {
            fileViewAddress: "",
            fileUpPath: "",
            fileViewPath: "",
        },
        copyItem: {
            state: false,
            address: "",
            path: "",
            fileName: "",
        },
        modalState: true,
        conn: "",
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
        [renewFileViewInfo]: (state, action) => {
            return { ...state, 
                fileViewInfo: {
                    fileViewAddress: action.payload.fileViewAddress,
                    fileUpPath: action.payload.fileUpPath,
                    fileViewPath: action.payload.fileViewPath,
                },
            };
        },
        [switchModalState]: (state, action) => {
            return { ...state, 
                modalState: action.payload.modalState,
            };
        },
        [renewCopyItem]: (state, action) => {
            return { ...state, 
                copyItem: {
                    state: action.payload.state,
                    address: action.payload.address,
                    path: action.payload.path,
                    fileName: action.payload.fileName,
                },
            };
        },
        [renewConnections]: (state, action) => {
            return { ...state, 
                conn: action.payload.conn,
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
    renewFileViewInfo,
    switchModalState,
    renewCopyItem,
    renewConnections,
}

export default store;