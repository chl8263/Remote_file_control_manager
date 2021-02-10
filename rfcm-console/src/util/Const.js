const FETCH_STATE = {
    FINE: "FINE",
}

const ROLE = {
    USER: "ROLE_USER",
    ADMIN: "ROLE_ADMIN",
}

const PAGE_ROUTE  = {
    LODING: "LODING",
    LOGIN: "LOGIN",
    SIGNUP: "SIGNUP",
    MAINBOARD: "MAINBOARD",
    ADMIN: "ADMIN",
}

const COLOR  = {
    WHITE: "#FFFFFFFF",
    light_background: "#F2F2F2"
}

const HTTP = {
    GET: "GET",
    POST: "POST",
    PUT: "PUT",
    DELETE: "DELETE",
    PATCH: "PATCH",

    // --- 2xx Success ---
    STATUS_OK: 200,
    STATUS_CREATED: 201,
    STATUS_ACCEPTED: 202,
    
    // --- 4xx Client Error ---
    STATUS_BAD_REQUEST: 400,
    STATUS_UNAUTHORIZED: 401,
    STATUS_NOT_FOUND: 404,
    
    SERVER_URL: "http://localhost:8081",
    BASIC_TOKEN_PREFIX: "Bearer ",
}

const MediaType = {
    JSON: "application/json",
    HAL_JSON: "application/hal+json",
    HTML: "text/html",
}

const CellUnitRoles = {
    CREATOR: "CREATOR",
    ADMIN: "ADMIN",
    USER: "USER",
}


export {FETCH_STATE, PAGE_ROUTE, HTTP, MediaType, ROLE, CellUnitRoles, COLOR};