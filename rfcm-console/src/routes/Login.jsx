import React, { useEffect, useState, useRef } from "react";
import { useCookies } from 'react-cookie';
import { connect } from "react-redux";
import $ from "jquery";

import { actionCreators } from "../store";
import { PAGE_ROUTE, HTTP, MediaType, ROLE} from "../util/Const";

import PreLoader from "../component/PreLoader";

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

    const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

    const [userId, setUserId] = useState("");
    const [password, setPassword] = useState("");
    const userIdRef = useRef(null);
    const passwordRef = useRef(null);

    useEffect(() => {
        history.pushState('','', '/Login');
        $(".preloader").fadeOut();
    }, []);

    const onChangeUserIdInput = (e) => {
        setUserId(e.target.value);
    };

    const onChangePasswordInput = (e) => {
        setPassword(e.target.value);
    };

    const onSubmitLoginForm = (e) => {
        e.preventDefault();

        if(userId === ""){
            alert("Please fill out 'id' field");
            userIdRef.current.focus();
        } else if(password === ""){
            alert("Please fill out 'Password' field");
            passwordRef.current.focus();
        } else {
            const accountInfo = {
                userId: userId,
                password: password
            }
            // s: Ajax ----------------------------------
            fetch(HTTP.SERVER_URL + "/auth", {
                method: HTTP.POST,
                headers: {
                    'Content-type': MediaType.JSON,
                    'Accept': MediaType.JSON
                },
                body: JSON.stringify(accountInfo)
                
            }).then(res => { if(!res.ok){ throw res; } return res; })
            .then(res => { return res.json(); })
            .then(json => {
                var JWT_TOKEN = json.token;
                var UID = json.uid;
                setCookie('JWT_TOKEN', JWT_TOKEN, { path: '/' });
                setCookie('UID', UID, { path: '/' });
                addJwtToken(JWT_TOKEN);
                switchMainBoard();

                const userInfo = {
                    accountId: userId,
                    accountName: userId,
                }
                addUserInfo(userInfo);
                
            }).catch(error => {
                alert("Please check account information.");
            });
            // e: Ajax ----------------------------------
        }
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
                        ref={userIdRef}
                        onChange={onChangeUserIdInput}
                        value={userId}
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
                        ref={passwordRef}
                        onChange={onChangePasswordInput}
                        value={password}
                    />
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
                        </Grid>
                        <Grid item>
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
    };
}

export default connect(mapStateToProps, mapDispathToProps) (Login);