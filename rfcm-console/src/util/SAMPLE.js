// ajax for get
//s: Ajax ----------------------------------
fetch(HTTP.SERVER_URL + `/api/accounts/${appInfo.userInfo.currentUserId}/cells`, {
    method: HTTP.GET,
    headers: {
        'Content-type': MediaType.JSON,
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
    }else if("_embedded" in res){
        console.log(res._embedded.cellEntityModelList);
        setCellList(res._embedded.cellEntityModelList);
    }
}).catch(error => {
    console.error(error);
    alert("Client unexpect error.");
});
// e: Ajax ----------------------------------

// ajax for post 
// s: Ajax ----------------------------------
const accountInfo = {
    accountname: userName,
    password: password
}
fetch(HTTP.SERVER_URL + "/api/accounts", {
    method: HTTP.POST,
    headers: {
        'Content-type': MediaType.JSON,
        'Accept': MediaType.HAL_JSON,
        'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
    },
    body: JSON.stringify(accountInfo)
}).then(res => {
    if(res.ok){        
        alert("Create account successfully");
        switchLogin();     
        throw(FETCH_STATE.FINE);
    }else {
        return res.json();
    }
}).then(json => {
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