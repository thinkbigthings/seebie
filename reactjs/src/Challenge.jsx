import React, {useEffect, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import {useParams} from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";
import DatePicker from "react-datepicker";
import useApiPost from "./hooks/useApiPost";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./utility/BasicHeaders";
import {Tab, Tabs} from "react-bootstrap";
import CollapsibleContent from "./component/CollapsibleContent";
import {PREDEFINED_CHALLENGES} from "./utility/Constants";
import SuccessModal from "./component/SuccessModal";
import CollapsibleChallenge from "./component/CollapsibleChallenge";


function emptyChallenge() {
    const suggestedEndDate = new Date();
    suggestedEndDate.setDate(suggestedEndDate.getDate() + 14);
    return {
        name: "",
        description: "",
        localStartTime: new Date(),
        localEndTime: suggestedEndDate
    };
}

function Challenge(props) {

    const {username} = useParams();

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpoint = `/api/user/${username}/challenge`;
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const [createdCount, setCreatedCount] = useState(0);
    const [showCreateSuccess, setShowCreateSuccess] = useState(false);
    const [showCreateChallenge, setShowCreateChallenge] = useState(false);
    const [showPredefinedChallenges, setShowPredefinedChallenges] = useState(false);
    const [challengeEdit, setChallengeEdit] = useState(emptyChallenge());

    const [savedChallenges, setSavedChallenges] = useState({
        current: null,
        upcoming: [],
        completed: []
    });

    const post = useApiPost();

    const saveData = () => {
        post(challengeEndpoint, {
            name: challengeEdit.name,
            description: challengeEdit.description,
            start: SleepDataManager.toIsoLocalDate(challengeEdit.localStartTime),
            finish: SleepDataManager.toIsoLocalDate(challengeEdit.localEndTime)
        })
        .then(clearChallengeEdit)
        .then(() => setShowCreateSuccess(true))
        .then(() => setCreatedCount(createdCount + 1));
    }

    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [createdCount]);

    const clearChallengeEdit = () => {
        setShowCreateChallenge(false);
        setChallengeEdit(emptyChallenge());
    }

    const updateChallenge = (updateValues) => {
        setChallengeEdit({...challengeEdit, ...updateValues});
    }

    const onSelectChallenge = (selectedChallenge) => {
        return () => {
            updateChallenge({
                name: selectedChallenge.name,
                description: selectedChallenge.description
            });
            swapModals();
        }
    }

    const swapModals = () => {
        setShowCreateChallenge( ! showCreateChallenge);
        setShowPredefinedChallenges( ! showPredefinedChallenges);
    }

    const currentChallengeElement = (savedChallenges.current !== null)
        ? <CollapsibleChallenge key="0" challenge={savedChallenges.current} />
        : <div className={"my-2"}>No current challenge</div>;

    return (
        <Container>

            <Modal centered={true} show={showCreateChallenge} onHide={clearChallengeEdit}>
                <Modal.Header closeButton>
                    <Modal.Title>Create Sleep Challenge</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Button variant="secondary" className={"app-highlight w-100 mb-3"} onClick={swapModals}>
                        Select from a list
                    </Button>
                    <form>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="challengeName" className="form-label">Short Name</label>
                            <input type="email" className="form-control" id="challengeName" placeholder=""
                                   value={challengeEdit.name}
                                   onChange={e => updateChallenge({name: e.target.value})}/>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label type="text" htmlFor="description" className="form-label">Description</label>
                            <textarea rows="8" className="form-control" id="description" placeholder=""
                                      value={challengeEdit.description}
                                      onChange={e => updateChallenge({description: e.target.value})}/>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="startDate" className="form-label">Start Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={date => updateChallenge({localStartTime: date})}
                                    selected={challengeEdit.localStartTime}/>
                            </div>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="endDate" className="form-label">End Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={date => updateChallenge({localEndTime: date})}
                                    selected={challengeEdit.localEndTime}/>
                            </div>
                        </Container>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    <div className="d-flex flex-row">
                        <Button className="me-3" variant="success" onClick={saveData}>Save</Button>
                        <Button className="" variant="secondary" onClick={clearChallengeEdit}>Cancel</Button>
                    </div>
                </Modal.Footer>
            </Modal>

            <Modal centered={true} show={showPredefinedChallenges} onHide={swapModals}>
                <Modal.Header closeButton>
                    <Modal.Title>Predefined Challenges</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="secondary">
                        Select from a pre-defined list here. Once selected, you'll be able to edit it and customize the name and dates.
                    </Alert>
                    <Container className="px-0 overflow-y-scroll h-50vh ">
                        {PREDEFINED_CHALLENGES.map((challenge, index) => {
                            return (
                                <CollapsibleContent key={index} title={challenge.name}>
                                    <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
                                    <Button variant="success" className="mt-2 w-100" onClick={onSelectChallenge(challenge)}>
                                        Select
                                    </Button>
                                </CollapsibleContent>
                            );
                        })}
                        </Container>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={swapModals}>
                        Back To Create Challenge
                    </Button>
                </Modal.Footer>
            </Modal>

            <SuccessModal title="Creation Success" showing={showCreateSuccess} handleClose={() => setShowCreateSuccess(false)}>
                Challenge created successfully! It will appear in your list of challenges.
            </SuccessModal>

            <NavHeader title="Sleep Challenge">
                <Button variant="success" disabled={false} onClick={() => setShowCreateChallenge(true)}>
                    <FontAwesomeIcon icon={faPlus}/>
                </Button>
            </NavHeader>

            <Container className="container mt-3 px-0">
                <Tabs defaultActiveKey="current" id="challenge-tabs">
                    <Tab eventKey="current" title="Current">
                        {currentChallengeElement}
                    </Tab>
                    <Tab eventKey="completed" title="Completed">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.completed.map((challenge, index) =>
                                <CollapsibleChallenge key={index} challenge={challenge} />
                            )}
                        </Container>
                    </Tab>
                    <Tab eventKey="upcoming" title="Future">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.upcoming.map((challenge, index) =>
                                <CollapsibleChallenge key={index} challenge={challenge} />
                            )}
                        </Container>
                    </Tab>
                </Tabs>
            </Container>

        </Container>
    );
}

export default Challenge;
