import React, {useState} from 'react';
import 'react-datepicker/dist/react-datepicker.css';
import Container from "react-bootstrap/Container";
import {Collapse} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faAngleDown} from "@fortawesome/free-solid-svg-icons";


function CollapsibleContent(props:{title: string, children: React.ReactNode}) {

    const {title} = props;

    const [expanded, setExpanded] = useState(false);

    const collapseIconRotation = expanded ? "fa-rotate-180" : ""

    return (

        <Container className={"pt-3 p-0 m-0"}>

            <Button variant="dark"
                    className={"w-100 text-start border border-light-subtle app-faq-button d-flex align-items-center"}
                    onClick={()=> setExpanded(!expanded)}
                    aria-controls="example-collapse-text"
                    aria-expanded={expanded}>

                {title}

                <FontAwesomeIcon className={"app-highlight me-2 mt-1 ms-auto " + collapseIconRotation} icon={faAngleDown} />


            </Button>

            <Collapse in={expanded}>
                <div className="border border-light-subtle rounded p-2">
                    {props.children}
                </div>
            </Collapse>

        </Container>

    );

}

export default CollapsibleContent;
