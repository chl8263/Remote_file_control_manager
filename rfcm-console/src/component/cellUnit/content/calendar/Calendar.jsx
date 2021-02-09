import React, { useEffect } from "react";
import PreLoader from "../../../PreLoader";
import $ from "jquery";

const Calendar = () => {

    useEffect(() => {
        $(".preloader").fadeOut(); // Remove preloader.
        var calendarApp = new CalendarApp();
        calendarApp.init();
    }, []);

    return (
        <>
            <PreLoader />
            <div className="page-breadcrumb">
                <div className="row">
                    <div className="col-12 d-flex no-block align-items-center">
                        <h4 className="page-title">Calendar</h4>
                        
                    </div>
                </div>
            </div>

            <div className="container-fluid">
                <div className="row">
                    <div className="col-md-12">
                        <div className="card">
                            <div className="">
                                <div className="row">
                                    <div className="col-lg-3 border-right p-r-0">
                                        <div className="card-body border-bottom">
                                            <h4 className="card-title m-t-10">{`Drag & Drop Event`}</h4>
                                        </div>
                                        <div className="card-body">
                                            <div className="row">
                                                <div className="col-md-12">
                                                    <div id="calendar-events" className="">
                                                        <div className="calendar-events m-b-20" data-classname="bg-info"><i className="fa fa-circle text-info m-r-10"></i>Event One</div>
                                                        <div className="calendar-events m-b-20" data-classname="bg-success"><i className="fa fa-circle text-success m-r-10"></i> Event Two</div>
                                                        <div className="calendar-events m-b-20" data-classname="bg-danger"><i className="fa fa-circle text-danger m-r-10"></i>Event Three</div>
                                                        <div className="calendar-events m-b-20" data-classname="bg-warning"><i className="fa fa-circle text-warning m-r-10"></i>Event Four</div>
                                                    </div>
                                                    <div className="custom-control custom-checkbox">
                                                        <input type="checkbox" className="custom-control-input" id="drop-remove"/>
                                                        <label className="custom-control-label" htmlFor="drop-remove">Remove after drop</label>
                                                    </div>
                                                    <a href="#!" data-toggle="modal" data-target="#add-new-event" className="btn m-t-20 btn-info btn-block waves-effect waves-light">
                                                            <i className="ti-plus"></i> Add New Event
                                                        </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-lg-9">
                                        <div className="card-body b-l calender-sidebar">
                                            <div id="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="modal none-border" id="my-event">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h4 className="modal-title"><strong>Add Event</strong></h4>
                                <button type="button" className="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body"></div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary waves-effect" data-dismiss="modal">Close</button>
                                <button type="button" className="btn btn-success save-event waves-effect waves-light">Create event</button>
                                <button type="button" className="btn btn-danger delete-event waves-effect waves-light" data-dismiss="modal">Delete</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="modal fade none-border" id="add-new-event">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h4 className="modal-title"><strong>Add</strong> a category</h4>
                                <button type="button" className="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            </div>
                            <div className="modal-body">
                                <form>
                                    <div className="row">
                                        <div className="col-md-6">
                                            <label className="control-label">Category Name</label>
                                            <input className="form-control form-white" placeholder="Enter name" type="text" name="category-name" />
                                        </div>
                                        <div className="col-md-6">
                                            <label className="control-label">Choose Category Color</label>
                                            <select className="form-control form-white" data-placeholder="Choose a color..." name="category-color">
                                                <option value="success">Success</option>
                                                <option value="danger">Danger</option>
                                                <option value="info">Info</option>
                                                <option value="primary">Primary</option>
                                                <option value="warning">Warning</option>
                                                <option value="inverse">Inverse</option>
                                            </select>
                                        </div>
                                    </div>
                                </form>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-danger waves-effect waves-light save-category" data-dismiss="modal">Save</button>
                                <button type="button" className="btn btn-secondary waves-effect" data-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Calendar;