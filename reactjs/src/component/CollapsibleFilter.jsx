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

    const {selectedStart, onChangeStart, selectedEnd, onChangeEnd, title, color, collapsed, onCollapseClick} = props;

    const collapseIconRotation = collapsed ? "" : "fa-rotate-180";

    return (
        <Container>
            <Row className={"pb-3"}>
                <Col className="col-12">
                    <Button variant="dark"
                            className={"w-100 text-start border border-light-subtle "}
                            style={{backgroundColor: color}}
                            onClick={onCollapseClick}
                            aria-controls="example-collapse-text"
                            aria-expanded={!collapsed}>

                        {title}
                        <FontAwesomeIcon className={"app-highlight me-2 mt-1 float-end " + collapseIconRotation} icon={faAngleDown} ></FontAwesomeIcon>
                    </Button>
                </Col>
            </Row>
            <Row>
                <Collapse in={!collapsed}>
                    {/* Collapse has trouble with a functional component as the direct child,
                        but it works great if you wrap a functional component with a non-functional component.
                         This could probably also be fixed with forwardRef */}
                    <div>
                        <DateRangePicker
                            selectedStart={selectedStart}
                            onChangeStart={onChangeStart}
                            selectedEnd={selectedEnd}
                            onChangeEnd={onChangeEnd} />
                    </div>
                </Collapse>
            </Row>
        </Container>

    );

}

export default CollapsibleFilter;
