import React, { useEffect, useState, useRef } from "react";

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
  }

TransitionComponent.propTypes = {
    /**
     * Show the component; triggers the enter or exit states
     */
    in: PropTypes.bool,
  };
  
const StyledTreeItem = withStyles((theme) => ({
  iconContainer: {
    '& .close': {
      opacity: 0.3,
    },
  },
  group: {
    marginLeft: 3,
    paddingLeft: 8,
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

const TreeViewItem = ( { address, upPath, currentDirectory, no} ) => {
  const classes = useStyles();
  const [items, setItems] = useState([""]);
  const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

  const getFileList = (e) => {
    console.log(111); 
      e.preventDefault();
      var itemsTempList = [];
      itemsTempList.push(1);
      itemsTempList.push(2);
      setItems(itemsTempList);
  }

  const getUnderDirectory = (e) => {
    console.log(upPath);
    console.log(currentDirectory);
    // s: Ajax ----------------------------------
    fetch(HTTP.SERVER_URL + `/api/file/directory/${address}/${upPath + currentDirectory}`, {
        method: HTTP.GET,
        headers: {
            'Content-type': MediaType.JSON,
            'Accept': MediaType.JSON,
            'Authorization': HTTP.BASIC_TOKEN_PREFIX + cookies.JWT_TOKEN,
            'Uid': cookies.UID
        },
    }).then(res => {
        if(!res.ok){
            throw res;
        }
        return res;
    }).then(res => {
        return res.json();
    }).then(json => {
      console.log(json);
      const list = json.payload.split('^');
      var rootTempList = [];
      list.forEach(x => {
        rootTempList.push(x);
      });
      rootTempList.shift();
      setRootDirectoryList(rootTempList);
    }).catch(error => {
      console.error(error);
      alert("Please check information.");
    });
    // e: Ajax ----------------------------------
  }

    return (
        <>
            <StyledTreeItem
              key={address + upPath + currentDirectory + no}
              nodeId={address + upPath + currentDirectory + no}
              onLabelClick={getFileList}
              onIconClick={getUnderDirectory}
              // onClick={test}
              label={ <span   style={{ width: 100}} > <FontAwesomeIcon icon={faFolder} /> {currentDirectory} </span> }>

              {items.map( x => {
                return <TreeViewItem address={address} upPath={upPath+currentDirectory} currentDirectory={x} no={no+1}/>;
              })}

            </StyledTreeItem>
        </>
    );
}

export default TreeViewItem;