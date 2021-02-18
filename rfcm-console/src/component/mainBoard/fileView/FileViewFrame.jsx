import React, { useEffect, useState, useRef } from "react";

import { PAGE_ROUTE, HTTP, MediaType, SOCK_REQ_TYPE} from "../../../util/Const";

import PropTypes from 'prop-types';
import clsx from 'clsx';
import { lighten, makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import TableSortLabel from '@material-ui/core/TableSortLabel';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import Checkbox from '@material-ui/core/Checkbox';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';
import DeleteIcon from '@material-ui/icons/Delete';
import FilterListIcon from '@material-ui/icons/FilterList';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faFileAlt, faFolder } from "@fortawesome/free-regular-svg-icons"
import { connect } from "react-redux";

import { useCookies } from "react-cookie";
import { actionCreators } from "../../../store";

function createData(name, dateModified, type, size, hiden) {
    return { name, dateModified, type, size, hiden };
}
  
const rows = [
createData('...', '', '', '', 'previous'),
// createData('Cupcake', 305, 'directory', 67, ''),
// createData('Donut', 452, 'directory', 51, ''),
// createData('Eclair', 262, 'directory', 24, ''),
// createData('Frozen yoghurt', 159, 'directory', 24, ''),
// createData('Gingerbread', 356, 'directory', 49, ''),
// createData('Honeycomb', 408, 'excel', 87, ''),
// createData('Ice cream sandwich', 237, 'excel', 37, ''),
// createData('Jelly Bean', 375, 'excel', 94, ''),
// createData('KitKat', 518, 'txt', 65, ''),
// createData('Lollipop', 392, 'txt', 98, ''),
// createData('Marshmallow', 318, 'txt', 81, ''),
// createData('Nougat', 360, 'file', 9, ''),
// createData('Oreo', 437, 'file', 63, ''),
// createData('KitKat1', 518, 'file', 65, ''),
// createData('Lollipop1', 392, 'file', 98, ''),
// createData('Marshmallow1', 318, 'file', 81, ''),
// createData('Nougat1', 360, 'file', 9, ''),
// createData('Oreo1', 437, 'file', 63, ''),
];
  
  function descendingComparator(a, b, orderBy) {
    if (b[orderBy] < a[orderBy]) {
      return -1;
    }
    if (b[orderBy] > a[orderBy]) {
      return 1;
    }
    return 0;
  }
  
  function getComparator(order, orderBy) {
    return order === 'desc'
      ? (a, b) => descendingComparator(a, b, orderBy)
      : (a, b) => -descendingComparator(a, b, orderBy);
  }
  
  function stableSort(array, comparator) {
    console.log("start");
      console.log(array)
    if(array.length > 1){
        //if(array.length[0])
        console.log("666666666666");
        console.log(array[0]);
        let tempPrevious = "";
        if(array[0].hiden === "previous"){
           tempPrevious = array[0];
           array.shift();
           console.log("11111");
        }
        console.log("333");
        console.log(tempPrevious);
        
        const stabilizedThis = array.map((el, index) => [el, index]);
        stabilizedThis.sort((a, b) => {
          const order = comparator(a[0], b[0]);
          if (order !== 0) return order;
          return a[1] - b[1];
        });
        console.log("77777777777");

        let result = stabilizedThis.map((el) => el[0]);
        if(tempPrevious !== ""){
            console.log(222222);
            result.unshift(tempPrevious);
            array.unshift(tempPrevious);
        }
        console.log(88888888888);
        console.log(result);
        
        return result;
    }
    return array;
  }
  
  const headCells = [
    { id: 'name', numeric: false, disablePadding: true, label: 'Name' },
    { id: 'date_modified', numeric: true, disablePadding: false, label: 'Date modified' },
    { id: 'type', numeric: true, disablePadding: false, label: 'Type' },
    { id: 'size', numeric: true, disablePadding: false, label: 'Size' },
  ];
  
  function EnhancedTableHead(props) {
    const { classes, onSelectAllClick, order, orderBy, numSelected, rowCount, onRequestSort } = props;
    const createSortHandler = (property) => (event) => {
      onRequestSort(event, property);
    };
  
    return (
      <TableHead>
        <TableRow>
          {/* <TableCell padding="checkbox">
            <Checkbox
              indeterminate={numSelected > 0 && numSelected < rowCount}
              checked={rowCount > 0 && numSelected === rowCount}
              onChange={onSelectAllClick}
              inputProps={{ 'aria-label': 'select all desserts' }}
            />
          </TableCell> */}
          {headCells.map((headCell) => (
            <TableCell
              style={{ backgroundColor : '#CCCCCC' }}
              key={headCell.id}
              align={headCell.numeric ? 'right' : 'left'}
              padding={headCell.disablePadding ? 'none' : 'default'}
              sortDirection={orderBy === headCell.id ? order : false}
            >
              <TableSortLabel
                active={orderBy === headCell.id}
                direction={orderBy === headCell.id ? order : 'asc'}
                onClick={createSortHandler(headCell.id)}
              >
                {headCell.label}
                {orderBy === headCell.id ? (
                  <span className={classes.visuallyHidden}>
                    {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                  </span>
                ) : null}
              </TableSortLabel>
            </TableCell>
          ))}
        </TableRow>
      </TableHead>
    );
  }
  
  EnhancedTableHead.propTypes = {
    classes: PropTypes.object.isRequired,
    numSelected: PropTypes.number.isRequired,
    onRequestSort: PropTypes.func.isRequired,
    onSelectAllClick: PropTypes.func.isRequired,
    order: PropTypes.oneOf(['asc', 'desc']).isRequired,
    orderBy: PropTypes.string.isRequired,
    rowCount: PropTypes.number.isRequired,
  };
  
  const useToolbarStyles = makeStyles((theme) => ({
    root: {
      paddingLeft: theme.spacing(2),
      paddingRight: theme.spacing(1),
    },
    highlight:
      theme.palette.type === 'light'
        ? {
            color: theme.palette.secondary.main,
            backgroundColor: lighten(theme.palette.secondary.light, 0.85),
          }
        : {
            color: theme.palette.text.primary,
            backgroundColor: theme.palette.secondary.dark,
          },
    title: {
      flex: '1 1 100%',
    },
  }));
  
  const EnhancedTableToolbar = (props) => {
    const classes = useToolbarStyles();
    const { numSelected, fileViewInfo } = props;
  
    return (
      <Toolbar
        className={clsx(classes.root, {
          [classes.highlight]: numSelected > 0,
        })}
      >
        {numSelected > 0 ? (
          <Typography className={classes.title} color="inherit" variant="subtitle1" component="div">
            {numSelected} selected
          </Typography>
        ) : (
          <Typography className={classes.title} variant="h6" id="tableTitle" component="div">
              {fileViewInfo.fileViewAddress !== "" && fileViewInfo.fileViewPath !== "" && "["}
            {fileViewInfo.fileViewAddress}
            {fileViewInfo.fileViewAddress !== "" && fileViewInfo.fileViewPath !== "" && "] "}
            {
                fileViewInfo.fileViewPath.replace(/\\/g, "|").replace(/\//g,"|").replace(/\|/g,"/").substr(1)
            }
          </Typography>
        )}
  
        {numSelected > 0 ? (
          <Tooltip title="Delete">
            <IconButton aria-label="delete">
              <DeleteIcon />
            </IconButton>
          </Tooltip>
        ) : (
          <Tooltip title="Filter list">
            <IconButton aria-label="filter list">
              <FilterListIcon />
            </IconButton>
          </Tooltip>
        )}
      </Toolbar>
    );
  };
  
  EnhancedTableToolbar.propTypes = {
    numSelected: PropTypes.number.isRequired,
  };
  
  const useStyles = makeStyles((theme) => ({
    root: {
      width: '100%',
    },
    paper: {
      width: '100%',
      marginBottom: theme.spacing(2),
    },
    table: {
      minWidth: 750,
    },
    visuallyHidden: {
      border: 0,
      clip: 'rect(0 0 0 0)',
      height: 1,
      margin: -1,
      overflow: 'hidden',
      padding: 0,
      position: 'absolute',
      top: 20,
      width: 1,
    },
  }));
  
const EnhancedTable = ({ fileViewInfo, renewFileViewInfo }) => {
    const classes = useStyles();
    const [order, setOrder] = React.useState('asc');
    const [orderBy, setOrderBy] = React.useState('name');
    const [selected, setSelected] = React.useState([]);
    const [page, setPage] = React.useState(0);
    const [dense, setDense] = React.useState(false);
    const [rowsPerPage, setRowsPerPage] = React.useState(5);

    const [cookies, setCookie, removeCookie] = useCookies(["JWT_TOKEN"]);

    const [fileList, setFileList] = useState([]);

    useEffect(() => {
        console.log("noti at fileView !!!!!!!!");
        console.log(fileViewInfo.fileViewAddress);
        console.log(fileViewInfo.fileViewPath);

        getFileData(fileViewInfo.fileViewAddress, fileViewInfo.fileViewPath);

    }, [fileViewInfo]);

    const getFileData = (address, path) => {

        if(address == null || address == undefined || address == ""
            || path == null || path == undefined || path == "") return;

        // s: Ajax ----------------------------------
        var fianlPath = path;
                
        fianlPath = fianlPath.replace(/\\/g, "|").replace(/\//g,"|");
        if(fianlPath.charAt(0) === '|'){
        fianlPath = fianlPath.substr(1);
        }

        console.log("final!");
        console.log(fianlPath);

        fetch(HTTP.SERVER_URL + `/api/file/${address}/${fianlPath}`, {
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
            console.log(5555555555);
            console.log(json);

            if(json === null || json === undefined){
                setFileList([]);
                alert(errorMsg);
                return;
            }
            
            if(json.error === true){
                setFileList([]);
                alert(error.errorMsg);
                return;
            }

            if(!json.responseData.root){
                console.log(999999);
                json.responseData.fileList.unshift(createData('...', '', '', '', 'previous'));
            }

            setFileList(json.responseData.fileList);

        }).catch(error => {
        console.error(error);
        setFileList([]);
        //alert(error.errorMsg);
        });
        // e: Ajax ----------------------------------
    }
  
    const handleRequestSort = (event, property) => {
        console.log(444444444);
        console.log(property);
      const isAsc = orderBy === property && order === 'asc';
      setOrder(isAsc ? 'desc' : 'asc');
      setOrderBy(property);
    };
  
    const handleSelectAllClick = (event) => {
      if (event.target.checked) {
        const newSelecteds = rows.map((n) => n.name);
        setSelected(newSelecteds);
        return;
      }
      setSelected([]);
    };
  
    const handleClick = (event, row) => {
        console.log(event);
        console.log(name);
        if(row.hiden === "previous") return;
        var name = row.name;
        const selectedIndex = selected.indexOf(name);
        let newSelected = [];
    
        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, name);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
            selected.slice(0, selectedIndex),
            selected.slice(selectedIndex + 1),
            );
        }
    
        setSelected(newSelected);
    };

    const handleDoubleClick = (event, row) => {
        console.log("더블클릭 !!!!!!!!");
        console.log(fileViewInfo.fileUpPath.substr(1));
        const address = fileViewInfo.fileViewAddress;
        let path = "";
        let upPath = "";
        if(row.hiden === "previous") {
            path = fileViewInfo.fileUpPath;
            upPath = path.replace(/\\/g, "|").replace(/\//g,"|").split("|");
            console.log(upPath);
            if(upPath.length > 2){
                upPath.pop();
                upPath = upPath.join('/');
            }else {
                upPath = "";
            }
        }else {
            path = fileViewInfo.fileViewPath + '|' + row.name;
            path = path;
            upPath = fileViewInfo.fileViewPath;
        }

        const fileViewInfo2 = {
            fileViewAddress: address,
            fileUpPath: upPath,
            fileViewPath: path,
        }

        console.log(fileViewInfo2);
    
        renewFileViewInfo(fileViewInfo2);

    };
  
    const handleChangePage = (event, newPage) => {
      setPage(newPage);
    };
  
    const handleChangeRowsPerPage = (event) => {
      setRowsPerPage(parseInt(event.target.value, 10));
      setPage(0);
    };
  
    const handleChangeDense = (event) => {
      setDense(event.target.checked);
    };
  
    const isSelected = (name) => selected.indexOf(name) !== -1;
  
    const emptyRows = rowsPerPage - Math.min(rowsPerPage, rows.length - page * rowsPerPage);
  
    return (
      <div className={classes.root}>
        <Paper className={classes.paper}>
          <EnhancedTableToolbar numSelected={selected.length} fileViewInfo={fileViewInfo}/>
          <TableContainer>
            <Table
              stickyHeader
              className={classes.table}
              aria-labelledby="tableTitle"
              //size={dense ? 'small' : 'medium'}
              size={'small'}
              aria-label="enhanced table"
            >
              <EnhancedTableHead                
                classes={classes}
                numSelected={selected.length}
                order={order}
                orderBy={orderBy}
                onSelectAllClick={handleSelectAllClick}
                onRequestSort={handleRequestSort}
                rowCount={rows.length}
              />
              <TableBody>
                {stableSort(fileList, getComparator(order, orderBy))
                  //.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                  .map((row, index) => {
                    const isItemSelected = isSelected(row.name);
                    //const labelId = `enhanced-table-checkbox-${index}`;
  
                    return (
                      <TableRow                        
                        hover
                        onClick={(event) => handleClick(event, row)}
                        onDoubleClick={(event) => handleDoubleClick(event, row)}
                        role="checkbox"
                        aria-checked={isItemSelected}
                        tabIndex={-1}
                        key={row.name}
                        selected={isItemSelected}
                      >
                        {/* <TableCell padding="checkbox">
                          <Checkbox
                            checked={isItemSelected}
                            inputProps={{ 'aria-labelledby': labelId }}
                          />
                        </TableCell> */}
                        <TableCell >
                            {row.type === "directory" && <FontAwesomeIcon icon={faFolder}  style={{"marginRight": "15px"}} />}
                            {row.type === "file"      && <FontAwesomeIcon icon={faFileAlt}  style={{"marginRight": "15px"}} />}
                            {row.name}
                        </TableCell>
                        <TableCell align="right">{row.dateModified}</TableCell>
                        <TableCell align="right">{row.type}</TableCell>
                        <TableCell align="right">{row.size}</TableCell>
                      </TableRow>
                    );
                  })}
                {emptyRows > 0 && (
                  <TableRow style={{ height: (dense ? 33 : 53) * emptyRows }}>
                    <TableCell colSpan={6} />
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
          {/* <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={rows.length}
            rowsPerPage={rowsPerPage}
            page={page}
            onChangePage={handleChangePage}
            onChangeRowsPerPage={handleChangeRowsPerPage}
          /> */}
        </Paper>
        {/* <FormControlLabel
          control={<Switch checked={dense} onChange={handleChangeDense} />}
          label="Dense padding"
        /> */}
      </div>
    );
}
  
const mapStateToProps = (state, ownProps) => {
    return { fileViewInfo: state.fileViewInfo };
}
  
const mapDispathToProps = (dispatch) => {
    return {
        renewFileViewInfo: (fileViewInfo) => dispatch(actionCreators.renewFileViewInfo(fileViewInfo)),
    };
}

export default connect(mapStateToProps, mapDispathToProps) (EnhancedTable);