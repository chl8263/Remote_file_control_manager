import React, { useEffect, useState, useRef } from "react";

import { PAGE_ROUTE, HTTP, MediaType, SOCK_REQ_TYPE} from "../../../util/Const";

import PropTypes from 'prop-types';
import { fade, makeStyles, withStyles } from '@material-ui/core/styles';
import TreeView from '@material-ui/lab/TreeView';
import TreeItem from '@material-ui/lab/TreeItem';
import Collapse from '@material-ui/core/Collapse';
import { useSpring, animated } from 'react-spring/web.cjs'; // web.cjs is required for IE 11 support

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faFileAlt, faFolder } from "@fortawesome/free-regular-svg-icons"
import { faAngleRight, faAngleDown } from "@fortawesome/free-solid-svg-icons"

import TreeViewParent from "../treeView/TreeViewParent"

import SockJS from 'sockjs-client';

function TransitionComponent(props) {
  const style = useSpring({
    from: { opacity: 2, transform: 'translate3d(20px,0,0)' },
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
    marginLeft: 7,
    paddingLeft: 18,
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
  
const LeftTree = () => {
  const classes = useStyles();
  const [connectionList, setConnectionList] = useState([]);

  useEffect(() => {
    let  sockJS = new SockJS("http://localhost:8081/ws");
    sockJS.onopen = function () {
      // send : connection으로 message를 전달
      // connection이 맺어진 후 가입(JOIN) 메시지를 전달
      sockJS.send(JSON.stringify(
        {
          reqType: SOCK_REQ_TYPE.CONNECTIONS,
        }
        ));
      //sockJS.send("hohoho");
    }

    sockJS.onmessage = function (e) {
      if(e !== null && e !== undefined && e.data !== null && e.data !== undefined ){
        const resultObj = JSON.parse(e.data);

        if(resultObj.reqType === SOCK_REQ_TYPE.CONNECTIONS){
          const connections = resultObj.payload.split('/');
          var connectionsTempList = [];
          connections.forEach(x => {
            connectionsTempList.push(x);
            console.log(x);
          });
          connectionsTempList.shift();
          setConnectionList(connectionsTempList);
        }
      }
    }  
  }, []);

  return (
    <>
      <TreeView
        className={classes.root}
        // defaultExpanded={['1']}
        defaultCollapseIcon={<FontAwesomeIcon icon={faAngleDown} />}
        defaultExpandIcon={<FontAwesomeIcon icon={faAngleRight} />}
        //onNodeToggle={handleChange}
        // defaultEndIcon={<FontAwesomeIcon icon={faFileAlt} />}
        >

        {connectionList.sort().map( x => {
          return <TreeViewParent key={x} address={x}  />;
        })}
      </TreeView>
    </>
  );
}

export default LeftTree;