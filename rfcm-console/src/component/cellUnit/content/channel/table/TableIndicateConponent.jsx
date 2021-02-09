import React from "react";

const TableIndicateConponent = ( { link, name, clickFuntion, isAble, isActive } ) => {

    const classString = () => {
        var str = "paginate_button page-item";
        if(!isAble) str += " disabled";
        if(isActive) str += " active";
        return str;
    }

    const onClickLink = e => {
        e.preventDefault();
        clickFuntion(link);
    }

    return(
        <>
            <li className={classString()}>
                <a onClick={onClickLink} href="#!" aria-controls="zero_config" data-dt-idx="0" tabIndex="0" className="page-link">{name}</a>
            </li>
        </>
    );
};

export default TableIndicateConponent;