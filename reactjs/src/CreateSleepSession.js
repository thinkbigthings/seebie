import React, {useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./useApiPost";
import useCurrentUser from "./useCurrentUser";
import 'react-datepicker/dist/react-datepicker.css';
import {SleepForm} from "./SleepForm";
import SleepDataManager from "./SleepDataManager";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";

function CreateSleepSession(props) {

    const {currentUser} = useCurrentUser();

    const sleepUrl = '/user/' + currentUser.username + '/sleep';

    const [sleepData, setSleepData] = useState(SleepDataManager.createInitSleepData());
    const [showModal, setShowModal] = useState(false);
    const post = useApiPost();

    const saveData = () => {

        const formattedData = SleepDataManager.format(sleepData);

        post(sleepUrl, formattedData)
            .then(result => setShowModal(false))
            .then(props.onSave);
    }

    return (
        <>
            <Button variant="secondary" className="px-4 py-2" onClick={() => setShowModal(true)}>
                <FontAwesomeIcon className="me-2" icon={faPlus} />Log Sleep
            </Button>

            <Modal show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Header closeButton>
                    <Modal.Title>Log Sleep</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <SleepForm setSleepData={setSleepData} sleepData={sleepData} />
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
