import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import {SleepForm} from "./SleepForm";
import useApiPut from "./useApiPut";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrash} from "@fortawesome/free-solid-svg-icons";
import useApiDelete from "./useApiDelete";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";


function EditSleep() {

    const navigate = useNavigate();

    const {username, sleepId} = useParams();

    const sleepEndpoint = '/user/' + username + '/sleep/' + sleepId;

    const [loaded, setLoaded] = useState(false);
    const [showDeleteWarning, setShowDeleteWarning] = useState(false);
    const [sleepData, setSleepData] = useState(SleepDataManager.createInitSleepData());
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
                <Button variant="danger"  onClick={()=>setShowDeleteWarning(true)}>
                    <FontAwesomeIcon className="me-2" icon={faTrash} />
                    Delete
                </Button>
            </NavHeader>

            <Container id="sleepFormWrapper" className="px-0">
                {loaded ? <SleepForm setSleepData={setSleepData} sleepData={sleepData} /> : <div />}
            </Container>

            <div className="d-flex flex-row">
                <Button className="me-3" variant="primary" onClick={onSave} >Save</Button>
                <Button  variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
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

        </Container>
    );
}

export default EditSleep;
