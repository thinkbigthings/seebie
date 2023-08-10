import React from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {Collapse} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDown} from "@fortawesome/free-solid-svg-icons";
import DateRangePicker from "./DateRangePicker";


function CollapsibleFilter(props) {

    const {selectStartDate, onStartSelection, selectEndDate, onEndSelection, title, collapsed, setCollapsed} = props;

    const collapseIconRotation = collapsed ? "" : "fa-rotate-180";

    return (
        <Container>
            <Row className={"pb-3"}>
                <Col className="col-12">
                    <Button variant="dark"
                            className={"w-100 text-start border border-light-subtle"}
                            onClick={setCollapsed}
                            aria-controls="example-collapse-text"
                            aria-expanded={!collapsed}>

                        {title}
                        <FontAwesomeIcon className={"me-2 mt-1 float-end " + collapseIconRotation} icon={faAngleDown} ></FontAwesomeIcon>
                    </Button>
                </Col>
            </Row>
            <Row>
                {/*{!collapsed && (*/}
                    <Collapse>

                        <DateRangePicker   selectedStart={selectStartDate}
                                           onChangeStart={onStartSelection}
                                           selectedEnd={selectEndDate}
                                           onChangeEnd={onEndSelection} />
                    </Collapse>
                {/*)}*/}
            </Row>
        </Container>

    );

}

export default CollapsibleFilter;
