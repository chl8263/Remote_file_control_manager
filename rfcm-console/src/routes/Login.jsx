import React, { useEffect, useState, useRef } from "react";
import $ from "jquery";
import { connect } from "react-redux";

import PreLoader from "../component/PreLoader";

import { actionCreators } from "../store";
import { PAGE_ROUTE, HTTP, MediaType, ROLE} from "../util/Const";

import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';


const useStyles = makeStyles((theme) => ({
    paper: {
        marginTop: theme.spacing(8),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    avatar: {
        margin: theme.spacing(1),
        backgroundColor: theme.palette.secondary.main,
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(1),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
}));

const Login = ( {switchSignUp,switchMainBoard, addJwtToken, addUserInfo} ) => {

    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const userNameRef = useRef(null);
    const passwordRef = useRef(null);

    useEffect(() => {
        history.pushState('','', '/Login');
        $(".preloader").fadeOut();
    }, []);

    const onChangeUserNameInput = (e) => {
        setUserName(e.target.value);
    };

    const onChangePasswordInput = (e) => {
        setPassword(e.target.value);
    };

    const onSubmitLoginForm = (e) => {

        // s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + "/accounts/ewan", {
                method: HTTP.GET,
                headers: {
                    'Content-type': MediaType.JSON,
                    'Accept': MediaType.JSON
                },
                // body: JSON.stringify(accountInfo)
                
            }).then(res => {
                if(!res.ok){
                    throw res;
                }
                return res;
            }).then(res => {
                return res.json();
            }).then(json => {
                var JWT_TOKEN = json.token;
                if(JWT_TOKEN !== "" || JWT_TOKEN !== null){
                    addJwtToken(JWT_TOKEN);
                    
                    fetch(HTTP.SERVER_URL + `/api/accounts/${userName}`, {
                        method: HTTP.GET,
                        headers: {
                            'Content-type': MediaType.JSON,
                            'Accept': MediaType.HAL_JSON,
                            'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
                        },
                    }).then(res => {
                        if(!res.ok){
                            throw res;
                        }
                        return res;
                    }).then(res => {
                        return res.json();
                    }).then(json => {
                        addUserInfo(json)
                        if(json.role === ROLE.USER){
                            switchMainBoard();
                        }else if(json.role === ROLE.ADMIN){
                            // TODO : switch ADMIN page..
                        }else {throw json;}
                    }).catch(error => {
                        console.error(error);
                        alert("Please check account again.");
                    });
                }
            }).catch(error => {
                alert("Please check account again.");
            });
        //     // e: Ajax ----------------------------------

        // console.log("111111");
        // e.preventDefault();
        // if(userName === ""){
        //     alert("Please fill out 'User name' field");
        //     userNameRef.current.focus();
        // } else if(password === ""){
        //     alert("Please fill out 'Password' field");
        //     passwordRef.current.focus();
        // } else {
        //     const accountInfo = {
        //         accountname: userName,
        //         password: password
        //     }
        //     // s: Ajax ----------------------------------
        //     fetch(HTTP.SERVER_URL + "/auth", {
        //         method: HTTP.POST,
        //         headers: {
        //             'Content-type': MediaType.JSON,
        //             'Accept': MediaType.JSON
        //         },
        //         body: JSON.stringify(accountInfo)
                
        //     }).then(res => {
        //         if(!res.ok){
        //             throw res;
        //         }
        //         return res;
        //     }).then(res => {
        //         return res.json();
        //     }).then(json => {
        //         var JWT_TOKEN = json.token;
        //         if(JWT_TOKEN !== "" || JWT_TOKEN !== null){
        //             addJwtToken(JWT_TOKEN);
                    
        //             fetch(HTTP.SERVER_URL + `/api/accounts/${userName}`, {
        //                 method: HTTP.GET,
        //                 headers: {
        //                     'Content-type': MediaType.JSON,
        //                     'Accept': MediaType.HAL_JSON,
        //                     'Authorization': HTTP.BASIC_TOKEN_PREFIX + JWT_TOKEN
        //                 },
        //             }).then(res => {
        //                 if(!res.ok){
        //                     throw res;
        //                 }
        //                 return res;
        //             }).then(res => {
        //                 return res.json();
        //             }).then(json => {
        //                 addUserInfo(json)
        //                 if(json.role === ROLE.USER){
        //                     switchMainBoard();
        //                 }else if(json.role === ROLE.ADMIN){
        //                     // TODO : switch ADMIN page..
        //                 }else {throw json;}
        //             }).catch(error => {
        //                 console.error(error);
        //                 alert("Please check account again.");
        //             });
        //         }
        //     }).catch(error => {
        //         alert("Please check account again.");
        //     });
        //     // e: Ajax ----------------------------------
        // }
    };

    const onclickSignUpBtn = () => {
        switchSignUp();
    };

    function Copyright() {
        return (
          <Typography variant="body2" color="textSecondary" align="center">
            {'Copyright © '}
            <Link color="inherit" href="https://material-ui.com/">
              Your Website
            </Link>{' '}
            {new Date().getFullYear()}
            {'.'}
          </Typography>
        );
    }

    

    const classes = useStyles();

    return(
        <>
            <PreLoader />
            <Container component="main" maxWidth="xs">
                <CssBaseline />
                <div className={classes.paper}>
                    <Avatar className={classes.avatar}>
                    <LockOutlinedIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                    Sign in
                    </Typography>
                    <form className={classes.form} noValidate>
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="id"
                        label="id"
                        name="id"
                        autoComplete="id"
                        autoFocus
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                    />
                    {/* <FormControlLabel
                        control={<Checkbox value="remember" color="primary" />}
                        label="Remember me"
                    /> */}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        className={classes.submit}
                        onClick={onSubmitLoginForm}
                    >
                        Sign In
                    </Button>
                    <Grid container>
                        <Grid item xs>
                        {/* <Link href="#" variant="body2">
                            Forgot password?
                        </Link> */}
                        </Grid>
                        <Grid item>
                        {/* <Link href="#" variant="body2">
                            {"Don't have an account? Sign Up"}
                        </Link> */}
                        </Grid>
                    </Grid>
                    </form>
                </div>
                <Box mt={8}>
                    <Copyright />
                </Box>
            </Container>
        </>
    );
};

const mapStateToProps = (state, ownProps) => {
    return state;
}

const mapDispathToProps = (dispatch) => {
    return {
        switchSignUp: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.SIGNUP)),
        switchMainBoard: () => dispatch(actionCreators.switchMainPageRoute(PAGE_ROUTE.MAINBOARD)),
        addJwtToken: (jwtToken) => dispatch(actionCreators.addJwtToken(jwtToken)),
        addUserInfo: (userInfo) => dispatch(actionCreators.addUserInfo(userInfo)),
        //addUserName: (username) => dispatch(actionCreators.addUserName(username)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (Login);