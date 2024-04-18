// @ts-nocheck
import React, {useEffect, useState} from 'react';
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";
import useApiPost from "./hooks/useApiPost";
import 'react-datepicker/dist/react-datepicker.css';
import {SleepForm} from "./SleepForm";
import {createInitSleepData} from "./utility/SleepDataManager";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {GET} from "./utility/BasicHeaders";
import {emptyChallengeList} from "./utility/Constants";
import CollapsibleContent from "./component/CollapsibleContent";
import {toSleepDto} from "./utility/Mapper.ts";

function CreateSleepSession(props) {

    const {onSave, username} = props;

    const sleepUrl = `/api/user/${username}/sleep`;

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const [sleepData, setSleepData] = useState(createInitSleepData());
    const [dataValid, setDataValid] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [openCount, setOpenCount] = useState(1);
    const [savedChallenges, setSavedChallenges] = useState(emptyChallengeList);

    const post = useApiPost();

    const saveData = () => {
        post(sleepUrl, JSON.stringify(toSleepDto(sleepData)))
            .then(closeModal)
            .then(onSave);
    }

    // load current challenges
    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [openCount]);

    const defaultTitle = "Log Sleep";

    const openModal = () => {
        setOpenCount(openCount + 1);
        setShowModal(true);
    }

    const closeModal = () => {
        setSleepData(createInitSleepData());
        setShowModal(false);
    }

    return (
        <>
            <Button variant="secondary" className="px-4 py-2" onClick={openModal}>
                <FontAwesomeIcon className="me-2" icon={faPlus} />Log Sleep
            </Button>

            <Modal show={showModal} onHide={closeModal} >
                <Modal.Header closeButton>
                    <Modal.Title className={"w-100"}>
                        {defaultTitle}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {savedChallenges.current.length === 0
                        ? <span />
                        : <CollapsibleContent title={"Challenge in progress"}>
                            <ul>
                                {savedChallenges.current.map(c => c.challenge).map(c =>
                                    <li key={c.name}>{c.name}</li>)
                                }
                            </ul>
                        </CollapsibleContent>
                    }
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
