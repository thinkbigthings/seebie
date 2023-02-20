import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import {SleepForm} from "./SleepForm";
import useApiPut from "./useApiPut";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./BasicHeaders";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrash} from "@fortawesome/free-solid-svg-icons";
import useApiDelete from "./useApiDelete";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";


function EditSleep({history, match}) {

    const username = match.params.username;
    const sleepId = match.params.sleepId;

    const sleepEndpoint = '/user/' + username + '/sleep/' + sleepId;

    const [loaded, setLoaded] = useState(false);

    const [showDeleteWarning, setShowDeleteWarning] = useState(false);

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

    const callDelete = useApiDelete();
    const deleteById = () => {
        callDelete("/user/" + username + "/sleep/" + sleepId)
            .then(history.goBack);
    }

    return (
        <div className="container mt-3">

            <Container className="d-flex justify-content-between" >
                <h1 >Sleep Session</h1>
                <Button variant="danger"  onClick={()=>setShowDeleteWarning(true)}>
                    <FontAwesomeIcon className="me-2" icon={faTrash} />
                    Delete
                </Button>
            </Container>

            <Container id="sleepFormWrapper" className="pl-0 pr-0">
                {loaded ? <SleepForm onChange={onChange} initData={sleepData} /> : <div />}
            </Container>

            <div className="d-flex flex-row-reverse">
                <Button className="m-1" variant="primary" onClick={onSave} >Save</Button>
                <Button className="m-1" variant="secondary" onClick={history.goBack}>Cancel</Button>
            </div>



            <Modal show={showDeleteWarning} onHide={() => setShowDeleteWarning(false)} >
                <Modal.Header closeButton>
                    <Modal.Title>Warning</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="warning">
                        This deletes the current sleep log entry and cannot be undone. Proceed?
                    </Alert>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteWarning(false)}>Cancel</Button>
                    <Button variant="warning" onClick={deleteById} >Delete</Button>
                </Modal.Footer>
            </Modal>

        </div>
    );
}

export default EditSleep;
