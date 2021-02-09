import React from "react";

const PreLoader = () => {
    return (
        <>
            {/* <!-- ============================================================== -->
            <!-- Preloader - style you can find in spinners.css -->
            <!-- ============================================================== --> */}
            <div className="preloader">
                <div className="lds-ripple">
                    <div className="lds-pos"></div>
                    <div className="lds-pos"></div>
                </div>
            </div>
            {/* <!-- ============================================================== -->
            <!-- Preloader - style you can find in spinners.css -->
            <!-- ============================================================== --> */}
        </>
    );
};

export default PreLoader;