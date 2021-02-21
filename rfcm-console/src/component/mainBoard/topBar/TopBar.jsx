import React, { useEffect, useState, useRef } from "react";

import { fade, makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import InputBase from '@material-ui/core/InputBase';
import Badge from '@material-ui/core/Badge';
import MenuItem from '@material-ui/core/MenuItem';
import Menu from '@material-ui/core/Menu';
import Button from '@material-ui/core/Button';
import MenuIcon from '@material-ui/icons/Menu';
import SearchIcon from '@material-ui/icons/Search';
import AccountCircle from '@material-ui/icons/AccountCircle';
import MailIcon from '@material-ui/icons/Mail';
import NotificationsIcon from '@material-ui/icons/Notifications';
import MoreIcon from '@material-ui/icons/MoreVert';
import { connect } from "react-redux";
import { actionCreators } from "../../../store";

const useStyles = makeStyles((theme) => ({
    grow: {
      flexGrow: 1,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    title: {
      display: 'none',
      [theme.breakpoints.up('sm')]: {
        display: 'block',
      },
    },
    search: {
      position: 'relative',
      borderRadius: theme.shape.borderRadius,
      backgroundColor: fade(theme.palette.common.white, 0.15),
      '&:hover': {
        backgroundColor: fade(theme.palette.common.white, 0.25),
      },
      marginRight: theme.spacing(2),
      marginLeft: 0,
      width: '100%',
      [theme.breakpoints.up('sm')]: {
        marginLeft: theme.spacing(3),
        width: 'auto',
      },
    },
    searchIcon: {
      padding: theme.spacing(0, 2),
      height: '100%',
      position: 'absolute',
      pointerEvents: 'none',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    },
    inputRoot: {
      color: 'inherit',
    },
    inputInput: {
      padding: theme.spacing(1, 1, 1, 0),
      // vertical padding + font size from searchIcon
      paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
      transition: theme.transitions.create('width'),
      width: '100%',
      [theme.breakpoints.up('md')]: {
        width: '20ch',
      },
    },
    sectionDesktop: {
      display: 'none',
      [theme.breakpoints.up('md')]: {
        display: 'flex',
      },
    },
    sectionMobile: {
      display: 'flex',
      [theme.breakpoints.up('md')]: {
        display: 'none',
      },
    },
  }));
  
  const TopBar = ({ fileViewInfo, copyItem, renewCopyItem }) => {
    const classes = useStyles();
    const [anchorEl, setAnchorEl] = React.useState(null);
    const [mobileMoreAnchorEl, setMobileMoreAnchorEl] = React.useState(null);
  
    const isMenuOpen = Boolean(anchorEl);
    const isMobileMenuOpen = Boolean(mobileMoreAnchorEl);
  
    const handleProfileMenuOpen = (event) => {
      setAnchorEl(event.currentTarget);
    };
  
    const handleMobileMenuClose = () => {
      setMobileMoreAnchorEl(null);
    };
  
    const handleMenuClose = () => {
      setAnchorEl(null);
      handleMobileMenuClose();
    };
  
    const handleMobileMenuOpen = (event) => {
      setMobileMoreAnchorEl(event.currentTarget);
    };

    const onClickMove = () => {
        console.log("click! Move");
        if(fileViewInfo.address == "" || fileViewInfo.path == ""){
          resetCopyItem();
        }

        if(fileViewInfo.fileViewAddress !== copyItem.address){
          alert("Cannot copy to another address");
          resetCopyItem();
        }

        // s: Ajax ----------------------------------
        var fianlPath = fileViewInfo.fileViewPath;
        if(fianlPath !== ""){
            fianlPath += "|";
        }
        console.log("!!!!!!!");
        console.log(fianlPath);
        fianlPath = fianlPath.replace(/\\/g, "|").replace(/\//g,"|");
        if(fianlPath.charAt(0) === '|'){
        fianlPath = fianlPath.substr(1);
        }
        console.log(fianlPath);

        const fileChangeInfo = {
            path: "",
            beforeName: originalFileName,
            afterName: changedName,
            extension: extension
        }

        fetch(HTTP.SERVER_URL + `/api/file/move/${address}`, {
            method: HTTP.PUT,
            headers: {
                'Content-type': MediaType.JSON,
                'Accept': MediaType.JSON,
                'Authorization': HTTP.BASIC_TOKEN_PREFIX + cookies.JWT_TOKEN,
                'Uid': cookies.UID
            },
            body: JSON.stringify(fileChangeInfo)
        }).then(res => {
            if(!res.ok){
                throw res;
            }
            return res;
        }).then(res => {
            return res.json();
        }).then(json => {
            console.log("}{}{}{}{{{}{{");
            console.log(json);

            console.log(extension);

            if(json === null || json === undefined){
                alert(errorMsg);
                return;
            }
            
            if(json.error === true){
                alert(error.errorMsg);
                return;
            }

            let aftername = "";
            if(extension === undefined || extension === null || extension === ""){
                aftername = changedName;
            }else {
                aftername = changedName + "." + extension;
            }
            changeFileName(selectedRow.name, aftername);

        }).catch(error => {
            console.error(error);
            alert(error.errorMsg);
        });
        // e: Ajax ----------------------------------
    };

    const resetCopyItem = () => {
      const item = {
          state: false,
          address: "",
          path: "",
      };
      renewCopyItem(item);
    }

    const onClickCopy = () => {
        console.log("click! Copy");
    };
  
    const menuId = 'primary-search-account-menu';
    const renderMenu = (
      <Menu
        anchorEl={anchorEl}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        id={menuId}
        keepMounted
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        open={isMenuOpen}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleMenuClose}>Profile</MenuItem>
        <MenuItem onClick={handleMenuClose}>My account</MenuItem>
      </Menu>
    );
  
    const mobileMenuId = 'primary-search-account-menu-mobile';
    const renderMobileMenu = (
      <Menu
        anchorEl={mobileMoreAnchorEl}
        anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        id={mobileMenuId}
        keepMounted
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        open={isMobileMenuOpen}
        onClose={handleMobileMenuClose}
      >
        <MenuItem>
          <IconButton aria-label="show 4 new mails" color="inherit">
            <Badge badgeContent={4} color="secondary">
              <MailIcon />
            </Badge>
          </IconButton>
          <p>Messages</p>
        </MenuItem>
        <MenuItem>
          <IconButton aria-label="show 11 new notifications" color="inherit">
            <Badge badgeContent={11} color="secondary">
              <NotificationsIcon />
            </Badge>
          </IconButton>
          <p>Notifications</p>
        </MenuItem>
        <MenuItem onClick={handleProfileMenuOpen}>
          <IconButton
            aria-label="account of current user"
            aria-controls="primary-search-account-menu"
            aria-haspopup="true"
            color="inherit"
          >
            <AccountCircle />
          </IconButton>
          <p>Profile</p>
        </MenuItem>
      </Menu>
    );
  
    return (
      <div className={classes.grow}>
        <AppBar position="static">
          <Toolbar>
            {/* <IconButton
              edge="start"
              className={classes.menuButton}
              color="inherit"
              aria-label="open drawer"
            >
              <MenuIcon />
            </IconButton> */}
            {/* <Typography className={classes.title} variant="h6" noWrap>
              Material-UI
            </Typography> */}
            {/* <div className={classes.search}>
              <div className={classes.searchIcon}>
                <SearchIcon />
              </div>
              <InputBase
                placeholder="Search…"
                classes={{
                  root: classes.inputRoot,
                  input: classes.inputInput,
                }}
                inputProps={{ 'aria-label': 'search' }}
              />
            </div> */}
            <div className={classes.grow} />
            <div className={classes.sectionDesktop}>
                {copyItem.state && (
                  <>
                    <Button
                      onClick={onClickMove}
                      variant="contained"
                      color="secondary"
                      className={classes.button}
                      style={{"marginRight": "15px"}}
                    >
                        Move
                    </Button>

                    <Button
                        onClick={onClickCopy}
                        variant="contained"
                        color="inherit"
                        className={classes.button}
                        style={{"marginRight": "15px", "color": "black"}}
                    >
                        Copy
                    </Button>
                  </>
                )}
                
              
            </div>
            <div className={classes.sectionMobile}>
              <IconButton
                aria-label="show more"
                aria-controls={mobileMenuId}
                aria-haspopup="true"
                onClick={handleMobileMenuOpen}
                color="inherit"
              >
                <MoreIcon />
              </IconButton>
            </div>
          </Toolbar>
        </AppBar>
        {renderMobileMenu}
        {renderMenu}
      </div>
    );
  }


const mapStateToProps = (state, ownProps) => {
    return { 
      fileViewInfo: state.fileViewInfo,
      copyItem: state.copyItem,
    };
}
  
const mapDispathToProps = (dispatch) => {
    return {
        renewCopyItem: (copyItem) => dispatch(actionCreators.renewCopyItem(copyItem)),
        renewFileViewInfo: (fileViewInfo) => dispatch(actionCreators.renewFileViewInfo(fileViewInfo)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (TopBar);