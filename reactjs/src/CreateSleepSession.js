import React, {useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./useApiPost";
import useCurrentUser from "./useCurrentUser";
import 'react-datepicker/dist/react-datepicker.css';
import {SleepForm} from "./SleepForm";
import SleepDataManager from "./SleepDataManager";

function CreateSleepSession(props) {

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const data = SleepDataManager.createInitSleepData();

    const post = useApiPost();
    const [showLogSleep, setShowLogSleep] = useState(false);

    const saveData = (data) => {
        post(sleepUrl, SleepDataManager.format(data))
            .then(result => setShowLogSleep(false))
            .then(props.onSave);
    }

    function hideModal() {
        setShowLogSleep(false);
    }

    return (
        <>
            <Button variant="success" onClick={() => setShowLogSleep(true)}>Log Sleep</Button>

            <Modal show={showLogSleep} onHide={hideModal} >
                <Modal.Header closeButton>
                    <Modal.Title>Log Sleep</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <SleepForm onCancel={hideModal} onSave={saveData} initData={data} />
                </Modal.Body>
            </Modal>
        </>
    );
}

export {CreateSleepSession};
