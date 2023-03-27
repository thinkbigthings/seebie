import React from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faPlus, faUpload} from "@fortawesome/free-solid-svg-icons";

function Tools() {


    return (
        <Container>

            <NavHeader title="Tools" />

            <p>
                <Button variant="secondary" >
                    <FontAwesomeIcon className="me-2" icon={faDownload} />
                    Download sleep data to CSV file
                </Button>
            </p>

            <p>
                <Button variant="secondary" >
                    <FontAwesomeIcon className="me-2" icon={faUpload} />
                    Upload CSV file with sleep data
                </Button>
            </p>

        </Container>
    );
}


export default Tools;
