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
import {emptyChallengeList} from "./utility/Constants";
import CollapsibleContent from "./component/CollapsibleContent";

function CreateSleepSession(props) {

    const {onSave, username} = props;

    const sleepUrl = `/api/user/${username}/sleep`;

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const [sleepData, setSleepData] = useState(SleepDataManager.createInitSleepData());
    const [dataValid, setDataValid] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [savedChallenges, setSavedChallenges] = useState(emptyChallengeList);

    const post = useApiPost();

    const saveData = () => {
        post(sleepUrl, SleepDataManager.format(sleepData))
            .then(closeModal)
            .then(onSave);
    }

    // load current challenge
    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [showModal]);

    const defaultTitle = "Log Sleep";

    const challengeTitles = savedChallenges.current.map(c => c.name);
    if(challengeTitles.length === 0) {
        challengeTitles.push("No Challenges are in progress");
    }

    console.log(challengeTitles)

    const closeModal = () => {
        setSleepData(SleepDataManager.createInitSleepData());
        setShowModal(false);
    }

    return (
        <>
            <Button variant="secondary" className="px-4 py-2" onClick={() => setShowModal(true)}>
                <FontAwesomeIcon className="me-2" icon={faPlus} />Log Sleep
            </Button>

            <Modal show={showModal} onHide={closeModal} >
                <Modal.Header closeButton>
                    <Modal.Title className={"w-100"}>
                        {defaultTitle}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <CollapsibleContent title={"Challenge in progress"}>
                        <ul>
                            {challengeTitles.map((title, index) => {
                                return (
                                    <li key={title}>
                                        {title}
                                    </li>
                                );
                            })}
                        </ul>
                    </CollapsibleContent>
                    <div className={"my-2"} />
                    <SleepForm setSleepData={setSleepData} sleepData={sleepData} setDataValid={setDataValid}/>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={ closeModal }>Cancel</Button>
                    <Button variant="primary" onClick={ saveData } disabled={!dataValid}>Save</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export {CreateSleepSession};
