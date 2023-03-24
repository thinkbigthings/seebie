import React, {useEffect, useState} from 'react';

import ReactJson from 'react-json-view'
import {GET} from "./BasicHeaders";
import SleepDataManager from "./SleepDataManager";

function Home() {

    // https://www.npmjs.com/package/react-json-view

    let [actuatorData, setActuatorData] = useState({})

    // TODO
        // test.get("/actuator", 401),
        // test.get("/actuator/flyway", 401),
        // test.get("/actuator/health", 401),
        // test.get("/actuator/info", 401),
        // test.get("/actuator/mappings", 401),
        // test.get("/actuator/sessions"

    useEffect(() => {
        fetch('/actuator/info', GET)
            .then(response => response.json())
            .then(setActuatorData)
    }, [setActuatorData]);

    return (
        <div className="container mt-3">

            <h1>System Info</h1>

            <ReactJson src={actuatorData} displayDataTypes={false} displayObjectSize={false} theme={"twilight"}/>



        </div>
    );

}

export default Home;
