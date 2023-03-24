import React, {useEffect, useState} from 'react';

import ReactJson from 'react-json-view'
import {GET} from "./BasicHeaders";

function Home() {

    let [actuatorData, setActuatorData] = useState({})

    // TODO
    // "/actuator"
    // "/actuator/flyway"
    // "/actuator/health"
    // "/actuator/info"
    // "/actuator/mappings"
    // "/actuator/sessions"

    useEffect(() => {
        fetch('/actuator/info', GET)
            .then(response => response.json())
            .then(setActuatorData)
    }, [setActuatorData]);

    // https://www.npmjs.com/package/react-json-view

    return (
        <div className="container mt-3">

            <h1>System</h1>

            <ReactJson name="info" src={actuatorData} displayDataTypes={false} displayObjectSize={false} theme={"twilight"}/>

        </div>
    );

}

export default Home;
