import React, { useEffect, useState } from "react";

import TableIndicateConponent from "../table/TableIndicateConponent";



const TableIndicator = ( { getCahnnelPostListByWholeUrl, getCahnnelPostListByPageNumber, pageInfo, linkInfo }) => {

    const BUNCH_OF_PAGES_NUMBER = 5;

    const [isFirst, setIsFirst] = useState(false);
    const [firstUrl, setFirstUrl] = useState("aa");

    const [isPrev, setIsPrev] = useState(false);
    const [prevUrl, setPrevUrl] = useState("");

    const [isNext, setIsNext] = useState(false);
    const [nextUrl, setNextUrl] = useState("");

    const [isLast, setIsLast] = useState(false);
    const [lastUrl, setLastUrl] = useState("");

    const [pageNumberList, setPageNumberList] = useState([]);

    const [totalElements, setTotalElements] = useState(0);


    useEffect(() => {
        if(Object.keys(pageInfo).length > 0 && Object.keys(linkInfo).length > 0){
            if("first" in linkInfo) {
                setIsFirst(true);
                setFirstUrl(linkInfo.first.href);
            }else {
                setIsFirst(false);
            }

            if("prev" in linkInfo) {
                setIsPrev(true);
                setPrevUrl(linkInfo.prev.href);
            }else {
                setIsPrev(false);
            }

            if("next" in linkInfo) {
                setIsNext(true);
                setNextUrl(linkInfo.next.href);
            }else {
                setIsNext(false);
            }

            if("last" in linkInfo) {
                setIsLast(true);
                setLastUrl(linkInfo.last.href);
            }else {
                setIsLast(false);
            }

            if("number" in pageInfo && "size" in pageInfo && "totalElements" in pageInfo && "totalPages" in pageInfo){
                const currentPage = pageInfo.number;
                const listSize = pageInfo.size;
                const totalElements = pageInfo.totalElements;
                const totalPages = pageInfo.totalPages;

                setTotalElements(totalElements);

                const tempBunchOfCurrentPage = currentPage/BUNCH_OF_PAGES_NUMBER;
                const bunchOfCurrentPage = parseInt(tempBunchOfCurrentPage)+1;
                const startPageNumber = ((bunchOfCurrentPage-1) * BUNCH_OF_PAGES_NUMBER)+1;
                let endPageNumber = (bunchOfCurrentPage) * BUNCH_OF_PAGES_NUMBER;
                if(endPageNumber >= totalPages){
                    endPageNumber = totalPages;
                }

                const numberList = new Array();
                for(let i = startPageNumber; i <= endPageNumber; i++){
                    var validatedActive;
                    if(currentPage == i-1) validatedActive = true;
                     else validatedActive = false;
                    const pageNumberData = {
                        link: i,
                        name: i,
                        clickFuntion: getCahnnelPostListByPageNumber,
                        isAble: true,
                        isActive: validatedActive,
                    };
                    numberList.push(pageNumberData);
                }
                setPageNumberList(numberList);
            }
        }
    }, [pageInfo, linkInfo]);

    return (
        <>
            <div className="row">
                <div className="col-sm-12 col-md-4">
                    <div className="dataTables_info" id="zero_config_info" role="status" aria-live="polite">Total elements are {totalElements}</div>
                </div>
                <div className="col-sm-12 col-md-5">
                    <div className="dataTables_paginate paging_simple_numbers" id="zero_config_paginate">
                        <ul className="pagination">
                            <TableIndicateConponent link={firstUrl} name="First" clickFuntion={getCahnnelPostListByWholeUrl} isAble={isFirst} isActive={false}/>
                            <TableIndicateConponent link={prevUrl} name="Prev" clickFuntion={getCahnnelPostListByWholeUrl} isAble={isPrev} isActive={false}/>
                            {pageNumberList.map(x => {
                                return <TableIndicateConponent key={x.name} link={x.link} name={x.name} clickFuntion={x.clickFuntion} isAble={true} isActive={x.isActive} />
                            })}
                            <TableIndicateConponent link={nextUrl} name="Next" clickFuntion={getCahnnelPostListByWholeUrl} isAble={isNext} isActive={false}/>
                            <TableIndicateConponent link={lastUrl} name="Last" clickFuntion={getCahnnelPostListByWholeUrl} isAble={isLast} isActive={false}/>
                        </ul>
                    </div>
                </div>
            </div>
        </>
    )
}

export default TableIndicator;