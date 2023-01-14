import React, {useEffect, useState} from 'react';

import Button from "react-bootstrap/Button";
import {REACT_APP_API_VERSION} from "./Constants";
import {faRedo} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import Container from "react-bootstrap/Container";


const styleByStatus = {
    "UP" : "text-success",
    "DOWN" : "text-danger",
    "UNKNOWN" : "text-warning"
};

function About() {

    const [serverStatus, setServerStatus] = useState("UNKNOWN");

    let fetchData = () => {
        fetch('/actuator/health')
            .then(response => setServerStatus("UP"))
            .catch(response => setServerStatus("DOWN"));
    }

    useEffect(fetchData, [setServerStatus]);

    return (
        <Container className="pt-5 pb-3">

            <h1>About</h1>
            <p>
                Client API Compatibility Version {REACT_APP_API_VERSION}
            </p>
            <p><span className={styleByStatus[serverStatus]}>
                {"Server is "+ serverStatus}</span>
            </p>
            <p>
                <Button variant="primary" onClick={fetchData}><FontAwesomeIcon className="me-2" icon={faRedo} />Refresh Status</Button>
            </p>
        </Container>
    );

}

export default About;
