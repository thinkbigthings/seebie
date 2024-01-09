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
import {GET} from "./utility/BasicHeaders";
import {Tab, Tabs} from "react-bootstrap";
import CollapsibleContent from "./component/CollapsibleContent";
import {emptyChallengeList, PREDEFINED_CHALLENGES} from "./utility/Constants";
import SuccessModal from "./component/SuccessModal";
import CollapsibleChallenge from "./component/CollapsibleChallenge";
import Form from "react-bootstrap/Form";
import {toChallengeDto, withExactTimes} from "./utility/Mapper";
import useApiDelete from "./hooks/useApiDelete";

function emptyEditableChallenge() {
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
    const callDelete = useApiDelete();

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpoint = `/api/user/${username}/challenge`;
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const [createdCount, setCreatedCount] = useState(0);
    const [deletedCount, setDeletedCount] = useState(0);
    const [showCreateSuccess, setShowCreateSuccess] = useState(false);
    const [showCreateChallenge, setShowCreateChallenge] = useState(false);
    const [showPredefinedChallenges, setShowPredefinedChallenges] = useState(false);
    const [editableChallenge, setEditableChallenge] = useState(emptyEditableChallenge());
    const [savedChallenges, setSavedChallenges] = useState(emptyChallengeList);

    // validation of the overall form, so we know whether to enable the save button
    // this is set as invalid to start so the name can be blank before showing validation messages
    const [dataValid, setDataValid] = useState(false);

    // validation of individual fields for validation feedback to the user
    const [dateOrderValid, setDateOrderValid] = useState(true);
    const [nameValid, setNameValid] = useState(true);
    const [nameUnique, setNameUnique] = useState(true);

    // this is a warning, so we don't disable the save button
    const [datesOverlap, setDatesOverlap] = useState(false);

    const post = useApiPost();

    const saveData = () => {
        post(challengeEndpoint, toChallengeDto(editableChallenge))
        .then(clearChallengeEdit)
        .then(() => setShowCreateSuccess(true))
        .then(() => setCreatedCount(createdCount + 1));
    }

    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(withExactTimes)
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [createdCount, deletedCount]);

    const clearChallengeEdit = () => {
        setShowCreateChallenge(false);
        setEditableChallenge(emptyEditableChallenge());
        setDateOrderValid(true);
        setNameValid(true);
        setNameUnique(true);
        setDatesOverlap(false)
        setDataValid(true);
    }

    const updateChallenge = (updateValues) => {

        let updatedChallengeForm = {...editableChallenge, ...updateValues};
        setEditableChallenge(updatedChallengeForm);

        let updatedDateOrderValid = updatedChallengeForm.localStartTime < updatedChallengeForm.localEndTime;
        setDateOrderValid(updatedDateOrderValid);

        let updatedNameValid = updatedChallengeForm.name !== '' && updatedChallengeForm.name.trim() === updatedChallengeForm.name
        setNameValid(updatedNameValid);

        let allSavedChallenges = savedChallenges.upcoming.concat(savedChallenges.completed).concat(savedChallenges.current);
        let updatedNameUnique = ! allSavedChallenges.some(c => c.challenge.name === updatedChallengeForm.name);
        setNameUnique(updatedNameUnique);

        setDataValid( updatedDateOrderValid && updatedNameValid && updatedNameUnique);

        // Query for challenges where the given start is between challenge start/finish and same for given finish
        let updatedDatesOverlap = allSavedChallenges.map(c=>c.challenge).some(c => {
            return (updatedChallengeForm.localStartTime >= c.exactStart && updatedChallengeForm.localStartTime <= c.exactFinish)
                || (updatedChallengeForm.localEndTime >= c.exactStart && updatedChallengeForm.localEndTime <= c.exactFinish);
        });
        setDatesOverlap(updatedDatesOverlap);
    }

    const onSelectChallenge = (selectedChallenge) => {
        updateChallenge({
            name: selectedChallenge.name,
            description: selectedChallenge.description
        });
        swapModals();
    }

    const swapModals = () => {
        setShowCreateChallenge( ! showCreateChallenge);
        setShowPredefinedChallenges( ! showPredefinedChallenges);
    }

    const deleteChallenge = (challengeId) => {
        const endpoint = `/api/user/${username}/challenge/${challengeId}`;
        callDelete(endpoint).then(() => setDeletedCount(deletedCount + 1));
    }

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
                    <Form>
                        <Container className="ps-0">
                            <label htmlFor="challengeName" className="form-label">Short Name</label>
                            <Form.Control.Feedback type="invalid"
                                                    className={"d-inline ms-1 " + ((!nameUnique) ? 'visible' : 'invisible')}>
                                This name is already used
                            </Form.Control.Feedback>
                            <Form.Control
                                type="text"
                                className="form-control"
                                id="challengeName"
                                placeholder=""
                                value={editableChallenge.name}
                                onChange={e => updateChallenge({name: e.target.value})}
                                isInvalid={!nameValid || !nameUnique}
                            />
                        </Container>
                        <Form.Control.Feedback type="invalid"
                                               className={"mh-24px d-block " + ((!nameValid) ? 'visible' : 'invisible')}>
                            Name cannot be empty or have space at the ends
                        </Form.Control.Feedback>
                        <Container className="ps-0 mb-3">
                            <label type="text" htmlFor="description" className="form-label">Description</label>
                            <textarea rows="6" className="form-control" id="description" placeholder=""
                                      value={editableChallenge.description}
                                      onChange={e => updateChallenge({description: e.target.value})}/>
                        </Container>

                        <Container className="ps-0">
                            <label htmlFor="startDate" className="form-label">Start Date</label>
                            <div>
                                <DatePicker className={"form-control " + ((!dateOrderValid) ? 'border-danger' : '')}
                                            id="startDate" dateFormat="MMMM d, yyyy"
                                            onChange={date => updateChallenge({localStartTime: date})}
                                            selected={editableChallenge.localStartTime}/>
                            </div>
                            <Form.Control.Feedback type="invalid"
                                                   className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                                Start date must be before end date
                            </Form.Control.Feedback>
                        </Container>
                        <Container className="ps-0">
                            <label htmlFor="endDate" className="form-label">End Date</label>
                            <div>
                                <DatePicker className={"form-control " + ((!dateOrderValid) ? 'border-danger' : '')}
                                            id="startDate" dateFormat="MMMM d, yyyy"
                                            onChange={date => updateChallenge({localEndTime: date})}
                                            selected={editableChallenge.localEndTime}/>
                            </div>
                            <Form.Control.Feedback type="invalid"
                                                   className={"mh-24px d-block " + ((!dateOrderValid) ? 'visible' : 'invisible')}>
                                End date must be after start date
                            </Form.Control.Feedback>
                        </Container>
                    </Form>
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
                                    <Button variant="success" className="mt-2 w-100" onClick={ () => onSelectChallenge(challenge)}>
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
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.current.map((saved, index) =>
                                <CollapsibleChallenge key={index} challenge={saved.challenge}
                                                      onDelete={() => deleteChallenge(saved.id)} />
                            )}
                        </Container>
                    </Tab>
                    <Tab eventKey="completed" title="Completed">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.completed.map((saved, index) =>
                                <CollapsibleChallenge key={index} challenge={saved.challenge}
                                                      onDelete={() => deleteChallenge(saved.id)} />
                            )}
                        </Container>
                    </Tab>
                    <Tab eventKey="upcoming" title="Future">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.upcoming.map((saved, index) =>
                                <CollapsibleChallenge key={index} challenge={saved.challenge}
                                                      onDelete={() => deleteChallenge(saved.id)} />
                            )}
                        </Container>
                    </Tab>
                </Tabs>
            </Container>

        </Container>
    );
}

export default Challenge;
