import React, {useState} from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {Collapse} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDown} from "@fortawesome/free-solid-svg-icons";


function CollapsibleFaq(props) {

    const {title} = props;

    const [expanded, setExpanded] = useState(false);

    const collapseIconRotation = expanded ? "fa-rotate-180" : ""

    function onToggle() {
        setExpanded(!expanded);
    }

    return (
        <Container>
            <Row className={"pt-3 "}>
                <Col className="col-12 px-0">
                    <Button variant="dark"
                            className={"w-100 text-start border border-light-subtle sb-faq-button"}
                            onClick={onToggle}
                            aria-controls="example-collapse-text"
                            aria-expanded={expanded}>

                        {title}

                        <FontAwesomeIcon className={"me-2 mt-1 float-end " + collapseIconRotation} icon={faAngleDown} ></FontAwesomeIcon>
                    </Button>
                </Col>
            </Row>
            <Row>
                {/* with a Card as the content, the collapse animation does not work */}
                <Collapse in={expanded}>
                    <div className="border border-light-subtle rounded py-2">
                        {props.children}
                    </div>
                </Collapse>
            </Row>
        </Container>

    );

}

export default CollapsibleFaq;
