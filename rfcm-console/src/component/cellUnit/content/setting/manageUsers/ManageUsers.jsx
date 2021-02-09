import React, { useEffect, useState } from "react";

import { PAGE_ROUTE, HTTP, MediaType} from "../../../../../util/Const";
import { connect } from "react-redux";
import ManageUsersRow from "./ManageUsersRow";

const ManageUsers = ({appInfo}) => {

    const cellId = appInfo.cellInfo.cellId;

    const [accountList, setAccountList] = useState([]);

    useEffect(() => {
        getAccountList();
    }, []);

    const getAccountList = () => {
        const JWT_TOKEN = appInfo.appInfo.jwtToken;
         // s: Ajax ----------------------------------
         fetch(HTTP.SERVER_URL + `/api/cells/${cellId}/accounts`, {
            method: HTTP.GET,
            headers: {
                'Accept': MediaType.HAL_JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
            },
        }).then(res => {
            return res.json();
        }).then(res => {
            if("errors" in res){
                try{
                    errorCodeToAlertCreater(json);
                }catch(error){
                    throw error;
                }
            }else{
                console.log(res);
                setAccountList([]);
                setAccountList(res._embedded.accountEntityModelList);
            }
        }).catch(error => {
            console.error(error);
            alert("Cannot load channel list");
        });
        // e: Ajax ----------------------------------
    };

    return (
        <>
            <div className="card">
                
                <table className="table">
                    <thead>
                        <tr >
                            <th scope="col">Account Name</th>
                            <th scope="col">Actions</th>
                        </tr>
                    </thead>
                    <tbody>

                        {accountList.map(x => {
                            return <ManageUsersRow key={x.channelId} accountInfo={x} getAccountList={getAccountList}/>
                        })}
                       
                    </tbody>
                </table>
            </div>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return { appInfo: state };
}

export default connect(mapStateToProps) (ManageUsers);