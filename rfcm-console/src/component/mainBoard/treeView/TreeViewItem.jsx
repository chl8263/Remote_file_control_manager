import React, { useEffect, useState, useRef } from "react";

import { actionCreators } from "../../../store";
import { connect } from "react-redux";
import { PAGE_ROUTE, HTTP, MediaType, SOCK_REQ_TYPE} from "../../../util/Const";

import PropTypes from 'prop-types';
import SvgIcon from '@material-ui/core/SvgIcon';
import { fade, makeStyles, withStyles } from '@material-ui/core/styles';
import TreeItem from '@material-ui/lab/TreeItem';
import Collapse from '@material-ui/core/Collapse';
import { useSpring, animated } from 'react-spring/web.cjs'; // web.cjs is required for IE 11 support

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faFileAlt, faFolder } from "@fortawesome/free-regular-svg-icons"
import { faAngleRight, faAngleDown } from "@fortawesome/free-solid-svg-icons"

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
};

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

const TreeViewItem = ( { address, upPath, currentDirectory, no, renewFileViewInfo, renewFileViewInfoAtTree} ) => {
  const classes = useStyles();
  const [directoryList, setDirectoryList] = useState([""]);
  const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

  // useEffect(() => {
  //   setDirectoryList([]);
  // }, []);

  const getFileList = (e) => {
    e.preventDefault();

    const fileViewInfo = {
      fileViewAddress: address,
      fileUpPath: upPath,
      fileViewPath: upPath+"|"+currentDirectory,
    }
    renewFileViewInfoAtTree(fileViewInfo);
  }

  const getUnderDirectory = (e) => {
    // s: Ajax ----------------------------------
    var fianlPath = upPath;
    if(fianlPath !== ""){
      fianlPath += "|";
    }
    fianlPath += currentDirectory;
    fianlPath = fianlPath.replace(/\\/g, "|").replace(/\//g,"|");
    if(fianlPath.charAt(0) === '|'){
      fianlPath = fianlPath.substr(1);
    }

    fetch(HTTP.SERVER_URL + `/api/file/directory/${address}/${fianlPath}`, {
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
      if(json === null || json === undefined){
        setDirectoryList([]);
        alert("Cannot get directory");
        return;
      }
      if(json.error === true){
        setDirectoryList([]);
        alert(json.errorMsg);
        return;
      }
      setDirectoryList(json.responseData);
    }).catch(error => {
      console.error(error);
      setDirectoryList([]);
      //alert(error.errorMsg);
    });
    // e: Ajax ----------------------------------
  };

  return (
      <>
          <StyledTreeItem
            key={address + upPath + currentDirectory + no}
            nodeId={address + upPath + currentDirectory + no}
            onLabelClick={getFileList}
            onIconClick={getUnderDirectory}
            // onClick={test}
            label={ <span   style={{ width: 100}} > <FontAwesomeIcon icon={faFolder} /> {currentDirectory} </span> }>

            {directoryList.map( x => {
              return <TreeViewItem key={address + upPath + currentDirectory + x + no} address={address} upPath={upPath+"/"+currentDirectory} currentDirectory={x} no={no+1} renewFileViewInfoAtTree={renewFileViewInfoAtTree}/>;
            })}

          </StyledTreeItem>
      </>
  );
}

const mapDispathToProps = (dispatch) => {
  return {
      renewFileViewInfo: (fileViewInfo) => dispatch(actionCreators.renewFileViewInfo(fileViewInfo)),
      renewFileViewInfoAtTree: (fileViewInfo) => dispatch(actionCreators.renewFileViewInfoAtTree(fileViewInfo)),
  };
}

export default connect(null, mapDispathToProps) (TreeViewItem);