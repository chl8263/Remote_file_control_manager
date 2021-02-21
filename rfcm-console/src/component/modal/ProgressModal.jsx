import React, { useEffect } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Modal from "@material-ui/core/Modal";
import CircularProgress from "@material-ui/core/CircularProgress";
import { connect } from "react-redux";
import { actionCreators } from "../../store";


function rand() {
  return Math.round(Math.random() * 20) - 10;
}

function getModalStyle() {
  const top = 50 + rand();
  const left = 50 + rand();

  return {
    top: `${top}%`,
    left: `${left}%`,
    transform: `translate(-${top}%, -${left}%)`
  };
}

const useStyles = makeStyles((theme) => ({
  paper: {
    position: "absolute",
    width: 400,
    backgroundColor: theme.palette.background.paper,
    border: "2px solid #000",
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3)
  }
}));

const ProgressModal = ({ modalState }) => {
  const classes = useStyles();
  // getModalStyle is not a pure function, we roll the style only on the first render
  const [modalStyle] = React.useState(getModalStyle);
  const [open, setOpen] = React.useState(false);

  useEffect(() => {
    if(modalState === null || modalState === undefined) return;
    console.log("&&&&&&&&&&&&");
    console.log(modalState);
    // if(modalState === true) {  setOpen(true) }
    // else if(!modalState === false) {  setOpen(false) }
  }, [modalState]);

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };


  return (
        <>
          <div>
          <Modal
              open={open}
              onClose={handleClose}
              aria-labelledby="simple-modal-title"
              aria-describedby="simple-modal-description"
              style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              minHeight: "100vh"
              }}
          >
              <CircularProgress />
          </Modal>
          </div>
        </>
);
//   return (
//       <>
//       {modalState && (
//           <>
//             <div>
//             <button type="button" onClick={handleOpen}>
//                 Open Modal
//             </button>
//             <Modal
//                 open={open}
//                 // onClose={handleClose}
//                 aria-labelledby="simple-modal-title"
//                 aria-describedby="simple-modal-description"
//                 style={{
//                 display: "flex",
//                 justifyContent: "center",
//                 alignItems: "center",
//                 minHeight: "100vh"
//                 }}
//             >
//                 <CircularProgress />
//             </Modal>
//             </div>
//           </>
//       )}
//     </>
//   );
}

const mapStateToProps = (state, ownProps) => {
    return {  };
}

const mapDispathToProps = (dispatch) => {
    return { };
}

export default connect(mapStateToProps, mapDispathToProps) (ProgressModal);