import React, { useEffect, useState, useRef } from "react";

import { PAGE_ROUTE, HTTP, MediaType} from "../../../util/Const";

import PropTypes from 'prop-types';
import SvgIcon from '@material-ui/core/SvgIcon';
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

function Root(props) {
  return (
    <SvgIcon fontSize="inherit" style={{ width: 14, height: 14 }} {...props}>
      {/* tslint:disable-next-line: max-line-length */}
      <path fill="currentColor" d="M15 20C15 19.45 14.55 19 14 19H13V17H19C20.11 17 21 16.11 21 15V7C21 5.9 20.11 5 19 5H13L11 3H5C3.9 3 3 3.9 3 5V15C3 16.11 3.9 17 5 17H11V19H10C9.45 19 9 19.45 9 20H2V22H9C9 22.55 9.45 23 10 23H14C14.55 23 15 22.55 15 22H22V20H15M5 15V7H19V15H5Z" />
    </SvgIcon>
  );
}

function OpenedFolder(props) {
  return (
    <SvgIcon fontSize="inherit" style={{ width: 14, height: 14 }} {...props}>
      {/* tslint:disable-next-line: max-line-length */}
      <path fill="currentColor" d="M19,20H4C2.89,20 2,19.1 2,18V6C2,4.89 2.89,4 4,4H10L12,6H19A2,2 0 0,1 21,8H21L4,8V18L6.14,10H23.21L20.93,18.5C20.7,19.37 19.92,20 19,20Z" />
    </SvgIcon>
  );
}
  
function ClosedFolder(props) {
  return (
    <SvgIcon fontSize="inherit" style={{ width: 14, height: 14 }} {...props}>
      {/* tslint:disable-next-line: max-line-length */}
      <path fill="currentColor" d="M20,18H4V8H20M20,6H12L10,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V8C22,6.89 21.1,6 20,6Z" />
    </SvgIcon>
  );
}

function CloseSquare(props) {
  return (
    <SvgIcon className="close" fontSize="inherit" style={{ width: 14, height: 14 }} {...props}>
      {/* tslint:disable-next-line: max-line-length */}
      <path d="M17.485 17.512q-.281.281-.682.281t-.696-.268l-4.12-4.147-4.12 4.147q-.294.268-.696.268t-.682-.281-.281-.682.294-.669l4.12-4.147-4.12-4.147q-.294-.268-.294-.669t.281-.682.682-.281.696 .268l4.12 4.147 4.12-4.147q.294-.268.696-.268t.682.281 .281.669-.294.682l-4.12 4.147 4.12 4.147q.294.268 .294.669t-.281.682zM22.047 22.074v0 0-20.147 0h-20.12v0 20.147 0h20.12zM22.047 24h-20.12q-.803 0-1.365-.562t-.562-1.365v-20.147q0-.776.562-1.351t1.365-.575h20.147q.776 0 1.351.575t.575 1.351v20.147q0 .803-.575 1.365t-1.378.562v0z" />
    </SvgIcon>
  );
}

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
  
const LeftTree = () => {
    const classes = useStyles();

    useEffect(() => {
      let sockJS = new SockJS("http://localhost:8081/test");
      sockJS.onopen = function () {
        // send : connection으로 message를 전달
        // connection이 맺어진 후 가입(JOIN) 메시지를 전달
        sockJS.send(JSON.stringify({chatRoomId: 123, type: 'JOIN', writer: 'Ewan'}));
        //sockJS.send("hohoho");
        
        // onmessage : message를 받았을 때의 callback
        sockJS.onmessage = function (e) {
            console.log(e.data);
            // var content = JSON.parse(e.data);
            // chatBox.append('<li>' + content.message + '(' + content.writer + ')</li>')
        }
      }
    });

    const sendMessage = () => {
      //socketio.emit("sendMessage", "hihih");
    };

    return (
        <>
            <TreeView
                className={classes.root}
                defaultExpanded={['1']}
                defaultCollapseIcon={<FontAwesomeIcon icon={faAngleDown} />}
                defaultExpandIcon={<FontAwesomeIcon icon={faAngleRight} />}
                // defaultEndIcon={<FontAwesomeIcon icon={faFileAlt} />}
                >
                <TreeViewParent />

                {/* <StyledTreeItem nodeId="1" label={ <span onClick={event => {console.log(111); event.preventDefault();}} style={{ width: 100}} > <FontAwesomeIcon icon={faFolder} /> programfiles </span> }>
                  <StyledTreeItem nodeId="2" label="Hello" />
                  <StyledTreeItem nodeId="3" label={ <span > <FontAwesomeIcon icon={faFolder} /> 11 </span> }  >
                      <StyledTreeItem nodeId="6" label="Hello" />
                      <StyledTreeItem nodeId="7" label="Sub-subtree with children">
                      <StyledTreeItem nodeId="9" label="Child 1" />
                      <StyledTreeItem nodeId="10" label="Child 2" />
                      <StyledTreeItem nodeId="11" label="Child 3" />
                      </StyledTreeItem>
                      <StyledTreeItem nodeId="8" label="Hello" />
                  </StyledTreeItem>
                  <StyledTreeItem nodeId="4" label="World" />
                  <StyledTreeItem nodeId="5" label="Something something" />
                </StyledTreeItem> */}
            </TreeView>
        </>
    );
}

export default LeftTree;