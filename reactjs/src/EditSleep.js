import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import {SleepForm} from "./SleepForm";
import useApiPut from "./useApiPut";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./BasicHeaders";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";


function EditSleep({history, match}) {

    const username = match.params.username;
    const sleepId = match.params.sleepId;

    const sleepEndpoint = '/user/' + username + '/sleep/' + sleepId;

    const [loaded, setLoaded] = useState(false);


    const initSleepData = SleepDataManager.createInitSleepData();
    const [sleepData, setSleepData] = useState(initSleepData);

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(SleepDataManager.parse)
            .then(setSleepData)
            .then(() => setLoaded(true))
    }, [setSleepData, sleepEndpoint]);

    const put = useApiPut();
    const onSave = () => {
        console.log("sending sleep data " + JSON.stringify(sleepData));
        put(sleepEndpoint, sleepData).then(history.goBack);
    }

    function onChange(updatedSleepData) {
        console.log("updated sleep data " + JSON.stringify(updatedSleepData));
        setSleepData(updatedSleepData);
    }

    return (
        <div className="container mt-3">

            <h1>Sleep Session</h1>

            <Container id="sleepFormWrapper" className="pl-0 pr-0">
                {loaded ? <SleepForm onChange={onChange} initData={sleepData} /> : <div />}
            </Container>

            <div className="d-flex flex-row-reverse">
                <Button className="m-1" variant="primary" onClick={onSave} >Save</Button>
                <Button className="m-1" variant="secondary" onClick={history.goBack}>Cancel</Button>
            </div>
        </div>
    );
}

export default EditSleep;
