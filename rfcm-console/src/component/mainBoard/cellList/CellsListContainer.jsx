import React from "react";

import CellsList from "../cellList/CellsList";

const CellsListContainer = ( { cellList, getCellList } ) => {

    const onClickReloadCellList = () => {
        getCellList();
    }

    return (
        <>
            <div className="card">
                <div className="card-body">
                    <span className="card-title">My Cell Unit</span>
                    <a href="#!" onClick={onClickReloadCellList} className="mdi mdi-reload" style={{"float": "right", "fontSize": "30px"}}></a>
                </div>
                
                {/* <div className="doScroll scroll-y"> */}
                <div className="doScroll scrollable"  style={{"height": "80vh"}}>
                    {cellList.map( x => {
                        return <CellsList key={x.cellId} cellInfo={x} />
                    })}
                </div>
            </div>
        </>
    );
};

export default CellsListContainer;