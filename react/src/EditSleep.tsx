import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import {SleepForm} from "./SleepForm";
import useApiPut from "./hooks/useApiPut";
import {createInitSleepData} from "./utility/SleepDataManager";
import {GET} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import useApiDelete from "./hooks/useApiDelete";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";
import WarningButton from "./component/WarningButton";
import {toLocalSleepData, toSleepDto} from "./utility/Mapper";
import {SleepDetailDto} from "./types/sleep.types";

function EditSleep() {

    const navigate = useNavigate();

    const {publicId, sleepId} = useParams();

    const sleepEndpoint = `/api/user/${publicId}/sleep/${sleepId}`;

    const [loaded, setLoaded] = useState(false);
    const [sleepData, setSleepData] = useState(createInitSleepData());
    const [dataValid, setDataValid] = useState(true);

    const put = useApiPut();
    const callDelete = useApiDelete();

    useEffect(() => {
        fetch(sleepEndpoint, GET)
            .then(response => response.json() as Promise<SleepDetailDto>)
            .then(toLocalSleepData)
            .then(setSleepData)
            .then(() => setLoaded(true))
    }, [setSleepData, sleepEndpoint]);

    const onSave = () => {
        put(sleepEndpoint, toSleepDto(sleepData)).then(() => navigate(-1));
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
