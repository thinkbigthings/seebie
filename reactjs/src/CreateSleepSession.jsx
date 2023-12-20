import React, {useEffect, useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./hooks/useApiPost";
import 'react-datepicker/dist/react-datepicker.css';
import {SleepForm} from "./SleepForm";
import SleepDataManager from "./SleepDataManager";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {GET} from "./utility/BasicHeaders";

function CreateSleepSession(props) {

    const {onSave, username} = props;

    const sleepUrl = `/api/user/${username}/sleep`;

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const [sleepData, setSleepData] = useState(SleepDataManager.createInitSleepData());
    const [showModal, setShowModal] = useState(false);
    const [savedChallenges, setSavedChallenges] = useState({
        current: null,
        upcoming: [],
        completed: []
    });

    const post = useApiPost();

    const saveData = () => {

        const formattedData = SleepDataManager.format(sleepData);

        post(sleepUrl, formattedData)
            .then(result => setShowModal(false))
            .then(onSave);
    }

    // load current challenge
    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, []);

    const defaultTitle = "Log Sleep";
    const hasCurrentChallenge = (savedChallenges.current !== null);
    const challengeTitle = hasCurrentChallenge ? savedChallenges.current.name : "";

    return (
        <>
            <Button variant="secondary" className="px-4 py-2" onClick={() => setShowModal(true)}>
                <FontAwesomeIcon className="me-2" icon={faPlus} />Log Sleep
            </Button>

            <Modal show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Header closeButton>
                    <Modal.Title className={"w-100"}>
                        {defaultTitle}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {hasCurrentChallenge && <div className={"app-highlight h6 alert alert-light "}>{challengeTitle}</div>}
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
