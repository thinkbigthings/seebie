import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import {SleepForm} from "./SleepForm";
import useApiPut from "./useApiPut";
import SleepDataManager from "./SleepDataManager";


function EditSleep({history, match}) {

    const username = match.params.username;
    const sleepId = match.params.sleepId;

    const sleepEndpoint = '/user/' + username + '/sleep/' + sleepId;

    const [loaded, setLoaded] = useState(false);

    const [data, setData] = useState(SleepDataManager.createInitSleepData());

    useEffect(() => {
        fetch(sleepEndpoint)
            .then(response => response.json())
            .then(SleepDataManager.parse)
            .then(setData)
            .then(() => setLoaded(true))
    }, [setData]);

    // update data

    const put = useApiPut();
    const onSave = (sleepData) => {
        put(sleepEndpoint, sleepData).then(history.goBack);
    }

    return (
        <div className="container mt-3">

            <h1>Sleep Session</h1>

            <Container id="sleepFormWrapper" className="pl-0 pr-0">
                {loaded ? <SleepForm onCancel={history.goBack} onSave={onSave} initData={data} /> : <div />}
            </Container>
        </div>
    );
}

export default EditSleep;
