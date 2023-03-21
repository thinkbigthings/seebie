import React, {useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./useApiPost";
import useCurrentUser from "./useCurrentUser";
import 'react-datepicker/dist/react-datepicker.css';
import {SleepForm} from "./SleepForm";
import SleepDataManager from "./SleepDataManager";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBed} from "@fortawesome/free-solid-svg-icons";

function CreateSleepSession(props) {

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const [sleepData, setSleepData] = useState(SleepDataManager.createInitSleepData());
    const [showModal, setShowModal] = useState(false);
    const post = useApiPost();

    const saveData = () => {
        post(sleepUrl, SleepDataManager.format(sleepData))
            .then(result => setShowModal(false))
            .then(props.onSave);
    }

    function updateSleepSession(updateValues) {
        let updatedSleep = {...sleepData, ...updateValues};
        if(SleepDataManager.isDataValid(updatedSleep)) {
            setSleepData(updatedSleep);
        }
    }

    return (
        <>
            <Button variant="primary" className="p-3" onClick={() => setShowModal(true)}>
                <FontAwesomeIcon className="me-2" icon={faBed} />Log Sleep
            </Button>

            <Modal show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Header closeButton>
                    <Modal.Title>Log Sleep</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <SleepForm onChange={updateSleepSession} data={sleepData} />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={ () => setShowModal(false) }>Cancel</Button>
                    <Button variant="primary" onClick={ saveData }>Save</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export {CreateSleepSession};
