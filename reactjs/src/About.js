import React, {useEffect, useState} from 'react';

import {REACT_APP_API_VERSION} from "./Constants";

const styleByStatus = {
    "UP" : "text-success",
    "DOWN" : "text-danger",
    "UNKNOWN" : "text-warning"
};

const initialData = {
    users: {
        count:0
    }
};

function About() {

    const [info, setInfo] = useState(initialData);

    useEffect(() => {
        fetch('/actuator/info')
            .then(response => response.json())
            .then(setInfo)
    }, [setInfo]);

    const [serverStatus, setServerStatus] = useState("UNKNOWN");

    useEffect(() => {
        fetch('/actuator/health')
            .then(response => setServerStatus("UP"))
            .catch(response => setServerStatus("DOWN"));
    }, [setServerStatus]);

    return (
        <div className="container mt-3">

            <h1>About</h1>
            <p>
                Seebie is a sleep diary and analysis tool to help you get better sleep
            </p>
            <p>
                Client API Compatibility Version {REACT_APP_API_VERSION}
            </p>
            <p>
                There are {info.users.count} users in the system.
            </p>
            <p><span className={styleByStatus[serverStatus]}>
                {"Server is "+ serverStatus}</span>
            </p>
        </div>
    );

}

export default About;
