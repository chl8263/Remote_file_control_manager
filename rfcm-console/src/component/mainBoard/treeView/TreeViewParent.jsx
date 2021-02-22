import React, { useEffect, useState, useRef } from "react";

import { connect } from "react-redux";
import { PAGE_ROUTE, HTTP, MediaType, SOCK_REQ_TYPE} from "../../../util/Const";

import PropTypes from 'prop-types';
import SvgIcon from '@material-ui/core/SvgIcon';
import { fade, makeStyles, withStyles } from '@material-ui/core/styles';
import TreeView from '@material-ui/lab/TreeView';
import TreeItem from '@material-ui/lab/TreeItem';
import Collapse from '@material-ui/core/Collapse';
import { useSpring, animated } from 'react-spring/web.cjs'; // web.cjs is required for IE 11 support

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faFileAlt, faFolder } from "@fortawesome/free-regular-svg-icons"
import { faNetworkWired } from "@fortawesome/free-solid-svg-icons"

import TreeViewItem from "../treeView/TreeViewItem"
import { useCookies } from "react-cookie";

function TransitionComponent(props) {
    const style = useSpring({
      from: { opacity: 0, transform: 'translate3d(20px,0,0)' },
      to: { opacity: props.in ? 1 : 0, transform: `translate3d(${props.in ? 0 : 20}px,0,0)` },
    });
  
    return (
      <animated.div style={style}>
        <Collapse {...props} />
      </animated.div>
    );
}

TransitionComponent.propTypes = {
    in: PropTypes.bool,
};
  
const StyledTreeItem = withStyles((theme) => ({
  iconContainer: {
    '& .close': {
      opacity: 0.3,
    },
  },
  group: {
    marginLeft: 5,
    paddingLeft: 15,
    borderLeft: `1px dashed ${fade(theme.palette.text.primary, 0.4)}`,
  },
}))((props) => <TreeItem {...props} TransitionComponent={TransitionComponent} />);

const useStyles = makeStyles({
    root: {
      height: 264,
      flexGrow: 1,
      maxWidth: 400,
    },
});

function NetworkIcon(props) {
  return (
    <SvgIcon fontSize="inherit" style={{ width: 14, height: 14 }} {...props}>
      {/* tslint:disable-next-line: max-line-length */}
      <path fill="currentColor" d="M15 20C15 19.45 14.55 19 14 19H13V17H19C20.11 17 21 16.11 21 15V7C21 5.9 20.11 5 19 5H13L11 3H5C3.9 3 3 3.9 3 5V15C3 16.11 3.9 17 5 17H11V19H10C9.45 19 9 19.45 9 20H2V22H9C9 22.55 9.45 23 10 23H14C14.55 23 15 22.55 15 22H22V20H15M5 15V7H19V15H5Z" />
    </SvgIcon>
  );
}

const TreeViewParent = ( { appInfo, address } ) => {
    const classes = useStyles();
    const [rootDirectoryList, setRootDirectoryList] = useState([""]);
    const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

    useEffect(() => {
    }, []);

    const getRootDirectory = (e) => {
      const errorMsg = "Cannot reach this Directory";
      e.preventDefault();

      console.log("1111111");

      // s: Ajax ----------------------------------
      fetch(HTTP.SERVER_URL + `/api/file/directory/${address}/root`, {
          method: HTTP.GET,
          headers: {
              'Content-type': MediaType.JSON,
              'Accept': MediaType.JSON,
              'Authorization': HTTP.BASIC_TOKEN_PREFIX + cookies.JWT_TOKEN,
              'Uid': cookies.UID
          },
      }).then(res => { if(!res.ok){ throw res; } return res;
      }).then(res => { return res.json();
      }).then(json => {
        console.log("2222");
        console.log(json);
        if(json === null || json === undefined){
          setRootDirectoryList([]);
          alert(errorMsg);
          return;
        }
        
        if(json.error === true){
          setRootDirectoryList([]);
          alert(error.errorMsg);
          return;
        }

        setRootDirectoryList(json.responseData);
        
      }).catch(error => {
        //console.error(error);
        console.log(json);
        setRootDirectoryList([]);
      });
      // e: Ajax ----------------------------------
    }

    return (
        <>
            <StyledTreeItem 
              key={address}
              nodeId={address} 
              onClick={getRootDirectory} 
              label={ <span style={{ width: 100}} > <NetworkIcon/> {address} </span> }>
                {rootDirectoryList.map( x => {
                  return <TreeViewItem key={address+x} address={address} upPath={""} currentDirectory={x} no={1}/>;
                })}
            </StyledTreeItem>
        </>
    );
}

const mapStateToProps = (state, ownProps) => {
  return { store: state };
}

export default connect(mapStateToProps) (TreeViewParent);