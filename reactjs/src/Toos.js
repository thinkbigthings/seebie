import React from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faUpload} from "@fortawesome/free-solid-svg-icons";
import {Link} from "react-router-dom";
import useCurrentUser from "./useCurrentUser";

function Tools() {

    const {currentUser} = useCurrentUser();

    const downloadUrl = "/user/" + currentUser.username + "/sleep/download";

    const downloadFile = () => {
        console.log("download logic goes here")
    }

    return (
        <Container>

            <NavHeader title="Tools" />

            <p>
                <a href={downloadUrl}>
                    <Button variant="secondary" onClick={downloadFile} >
                        <FontAwesomeIcon className="me-2" icon={faDownload} />
                        Download sleep data to CSV file
                    </Button>
                </a>
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
