import React, {useEffect, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faExclamationTriangle, faPlus} from "@fortawesome/free-solid-svg-icons";
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
import Form from "react-bootstrap/Form";


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

    // validation of the overall form so we know whether to enable the save button
    // this is set as invalid to start so the name can be blank before showing validation messages
    const [dataValid, setDataValid] = useState(false);

    // validation of individual fields for validation feedback to the user
    const [dateOrderValid, setDateOrderValid] = useState(true);
    const [nameValid, setNameValid] = useState(true);
    const [nameUnique, setNameUnique] = useState(true);

    // this is a warning, so we don't disable the save button
    const [datesOverlap, setDatesOverlap] = useState(false);

    // TODO we use raw form, try Form

    // TODO if possible, turn the date pickers red if invalid


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
        setDateOrderValid(true);
        setNameValid(true);
        setNameUnique(true);
        setDatesOverlap(false)
        setDataValid(true);
    }

    const updateChallenge = (updateValues) => {

        let updatedChallengeForm = {...challengeEdit, ...updateValues};

        setChallengeEdit(updatedChallengeForm);

        let updatedDateOrderValid = updatedChallengeForm.localStartTime < updatedChallengeForm.localEndTime;
        setDateOrderValid(updatedDateOrderValid);

        console.log("updatedChallengeForm.name: [" + updatedChallengeForm.name+ "]");
        let updatedNameValid = updatedChallengeForm.name !== '' && updatedChallengeForm.name.trim() === updatedChallengeForm.name
        setNameValid(updatedNameValid);

        let allSavedChallenges = savedChallenges.upcoming.concat(savedChallenges.completed);
        if(savedChallenges.current !== null) {
            allSavedChallenges.push(savedChallenges.current);
        }

        let updatedNameUnique = allSavedChallenges.filter(c => c.name === updatedChallengeForm.name).length === 0;
        setNameUnique(updatedNameUnique);

        // Query for challenges where the given start is between challenge start/finish and same for given finish
        let updatedDatesOverlap = allSavedChallenges.some(c => {
                let challengeStart = new Date(c.start);
                let challengeEnd = new Date(c.finish);
                return (updatedChallengeForm.localStartTime >= challengeStart && updatedChallengeForm.localStartTime <= challengeEnd)
                    || (updatedChallengeForm.localEndTime >= challengeStart && updatedChallengeForm.localEndTime <= challengeEnd);
            });

        setDatesOverlap(updatedDatesOverlap);

        setDataValid( updatedDateOrderValid && updatedNameValid && updatedNameUnique);
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
                        <Container className="ps-0">
                            <label htmlFor="challengeName" className="form-label">Short Name</label>
                            <Form.Control.Feedback
                                type="invalid"
                                className={"d-inline ms-1 " + ((!nameUnique) ? 'visible' : 'invisible')}>
                                This name is already used
                            </Form.Control.Feedback>
                            <Form.Control
                                type="text"
                                className="form-control"
                                id="challengeName"
                                placeholder=""
                                value={challengeEdit.name}
                                onChange={e => updateChallenge({name: e.target.value})}
                                isInvalid={!nameValid || !nameUnique}
                            />
                        </Container>
                        <Form.Control.Feedback
                            type="invalid"
                            className={"mh-24px d-block " + ((!nameValid) ? 'visible' : 'invisible')}>
                            Name cannot be empty or have space at the ends
                        </Form.Control.Feedback>
                        <Container className="ps-0 mb-3">
                            <label type="text" htmlFor="description" className="form-label">Description</label>
                            <textarea rows="6" className="form-control" id="description" placeholder=""
                                      value={challengeEdit.description}
                                      onChange={e => updateChallenge({description: e.target.value})}/>
                        </Container>

                        <Container className="ps-0">
                            <label htmlFor="startDate" className="form-label">Start Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={date => updateChallenge({localStartTime: date})}
                                    selected={challengeEdit.localStartTime}/>
                            </div>
                            <Form.Control.Feedback
                                type="invalid"
                                className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                                Start date must be before end date
                            </Form.Control.Feedback>
                        </Container>
                        <Container className="ps-0">
                            <label htmlFor="endDate" className="form-label">End Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={date => updateChallenge({localEndTime: date})}
                                    selected={challengeEdit.localEndTime}/>
                            </div>
                            <Form.Control.Feedback
                                type="invalid"
                                className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                                End date must be after start date
                            </Form.Control.Feedback>
                        </Container>
                    </form>
                    <label className={"text-warning " + ((datesOverlap) ? 'visible' : 'invisible')}>
                        <FontAwesomeIcon icon={faExclamationTriangle} className={"pe-1"}/>
                        This date range overlaps another challenge which is not recommended
                    </label>
                </Modal.Body>
                <Modal.Footer>
                    <div className="d-flex flex-row">
                        <Button className="me-3" variant="success" onClick={saveData}
                                disabled={!dataValid}>Save</Button>
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
