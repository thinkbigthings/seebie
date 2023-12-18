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

function Challenge(props) {

    const {username} = useParams();

    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpoint = `/api/user/${username}/challenge`;
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const suggestedEndDate = new Date();
    suggestedEndDate.setDate(suggestedEndDate.getDate()+14);

    const [createdCount, setCreatedCount] = useState(0);
    const [showCreateSuccess, setShowCreateSuccess] = useState(false);
    const [showCreateChallenge, setShowCreateChallenge] = useState(false);
    const [challenge, setChallenge] = useState({
        name: "",
        description: "",
        localStartTime: new Date(),
        localEndTime: suggestedEndDate
    });
    const [savedChallenges, setSavedChallenges] = useState({
        current: null,
        upcoming: [],
        completed: []
    });

    const post = useApiPost();

    const saveData = () => {
        post(challengeEndpoint, {
            name: challenge.name,
            description: challenge.description,
            start: SleepDataManager.toIsoLocalDate(challenge.localStartTime),
            finish: SleepDataManager.toIsoLocalDate(challenge.localEndTime)
        })
        .then(() => {
            setShowCreateChallenge(false);
            setShowCreateSuccess(true);
            setCreatedCount(createdCount + 1);
        });
    }

    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [createdCount]);

    const updateChallenge = (updateValues) => {
        setChallenge({...challenge, ...updateValues});
    }

    const hasCurrent = (savedChallenges.current !== null);

    return (
        <Container>

            <Modal centered={true} show={showCreateChallenge} onHide={() => setShowCreateChallenge(false)} >
                <Modal.Header closeButton>
                    <Modal.Title>Create Your Sleep Challenge</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="challengeName" className="form-label">Challenge Name</label>
                            <input type="email" className="form-control" id="challengeName" placeholder=""
                                   value={challenge.name}
                                   onChange={e => updateChallenge({name : e.target.value })} />
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label type="text" htmlFor="description" className="form-label">Description</label>
                            <textarea rows="8" className="form-control" id="description" placeholder=""
                                      value={challenge.description}
                                      onChange={e => updateChallenge({description : e.target.value })} />
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="startDate" className="form-label">Start Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={ date => updateChallenge({localStartTime : date })}
                                    selected={challenge.localStartTime} />
                            </div>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="endDate" className="form-label">End Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={ date => updateChallenge({localEndTime : date })}
                                    selected={challenge.localEndTime} />
                            </div>
                        </Container>
                    </form>
                </Modal.Body>
                <Modal.Footer>
                    <div className="d-flex flex-row">
                        <Button className="me-3" variant="success"
                                onClick={saveData}>Save</Button>
                        <Button className="" variant="secondary"
                                onClick={() => setShowCreateChallenge(false)}>Cancel</Button>
                    </div>
                </Modal.Footer>
            </Modal>

            <Modal centered={true} show={showCreateSuccess} onHide={() => setShowCreateSuccess(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Creation Success</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="success">
                        Challenge created successfully! It will appear in your list of challenges.
                    </Alert>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="success" onClick={() => setShowCreateSuccess(false)}>OK</Button>
                </Modal.Footer>
            </Modal>

            <NavHeader title="Sleep Challenge">
                <Button variant="success" disabled={false} onClick={() => setShowCreateChallenge(true)}>
                    <FontAwesomeIcon icon={faPlus}/>
                </Button>
            </NavHeader>

            <Container className="container mt-3 px-0">
                {hasCurrent
                    ? `Current Challenge: ${savedChallenges.current.name} from ${savedChallenges.current.start} to ${savedChallenges.current.finish}`
                    : "No current challenge"}
            </Container>

        </Container>
    );
}

export default Challenge;
