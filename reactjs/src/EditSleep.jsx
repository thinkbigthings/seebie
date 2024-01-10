import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import {SleepForm} from "./SleepForm";
import useApiPut from "./hooks/useApiPut";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import useApiDelete from "./hooks/useApiDelete";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";
import WarningButton from "./component/WarningButton";

function EditSleep() {

    const navigate = useNavigate();

    const {username, sleepId} = useParams();

    const sleepEndpoint = `/api/user/${username}/sleep/${sleepId}`;

    const [loaded, setLoaded] = useState(false);
    const [sleepData, setSleepData] = useState(SleepDataManager.createInitSleepData());
    const [dataValid, setDataValid] = useState(true);

    const put = useApiPut();
    const callDelete = useApiDelete();

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json())
            .then(SleepDataManager.parse)
            .then(setSleepData)
            .then(() => setLoaded(true))
    }, [setSleepData, sleepEndpoint]);

    const onSave = () => {
        put(sleepEndpoint, sleepData).then(() => navigate(-1));
    }

    const deleteById = () => {
        callDelete(sleepEndpoint)
            .then(() => navigate(-1));
    }

    return (
        <Container>

            <NavHeader title="Sleep Details">
                <WarningButton buttonText="Delete" onConfirm={deleteById}>
                    This deletes the current sleep log entry and cannot be undone. Proceed?
                </WarningButton>
            </NavHeader>

            <Container id="sleepFormWrapper" className="px-0">
                {loaded
                    ? <SleepForm setSleepData={setSleepData} sleepData={sleepData} setDataValid={setDataValid} />
                    : <div /> }
            </Container>

            <div className="d-flex flex-row">
                <Button className="me-3" variant="primary" onClick={onSave} disabled={ ! dataValid} >Save</Button>
                <Button  variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
            </div>

        </Container>
    );
}

export default EditSleep;
