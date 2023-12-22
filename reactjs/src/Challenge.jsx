import React, {useEffect, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus, faQuestion} from "@fortawesome/free-solid-svg-icons";
import {useParams} from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";
import DatePicker from "react-datepicker";
import useApiPost from "./hooks/useApiPost";
import SleepDataManager from "./SleepDataManager";
import {GET} from "./utility/BasicHeaders";
import {Tab, Tabs} from "react-bootstrap";
import CollapsibleContent from "./component/CollapsibleContent";


const predefinedChallenges = [
    {
        title: "Consistent bedtime",
        description: "Go to bed at the same time every day even on weekends"
    },
    {
        title: "Proper wind down",
        description: "Do something quiet and relaxing in low light for 1-2 hours before bed instead of looking at your phone or watching a show"
    },
    {
        title: "Reduce Caffeine",
        description: "Cut your caffeine in half, finish all caffeine before lunch, or eliminate caffeine entirely"
    },
    {
        title: "No clock",
        description: "Don't look at the clock during the night"
    },
    {
        title: "Natural sunlight",
        description: "Get 30 minutes of natural sunlight outside as early in the day as you can"
    },
    {
        title: "Meditate daily",
        description: "Meditate at the same time every day, even just 5 minutes."
    },
    {
        title: "Cool temperature",
        description: "Set the overnight temperature in your bedroom to 60-68 degrees Fahrenheit, and/or use a cooling mattress"
    },
    {
        title: "Maintenance",
        description: "Maintain current habits for next three months to make sure your sleep doesn't degrade over time"
    }
];

function calculateProgress(start, now, end) {

    const totalDuration = end - start;
    const durationFromStartToNow = now - start;
    const effectiveDuration = Math.min(durationFromStartToNow, totalDuration);

    return Math.round((effectiveDuration / totalDuration) * 100);
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

    const suggestedEndDate = new Date();
    suggestedEndDate.setDate(suggestedEndDate.getDate() + 14);

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

    const selectPressed = () => {
        setShowPredefinedChallenges(true);
        setShowCreateChallenge(false);
    }


    const hasCurrentChallenge = (savedChallenges.current !== null);

    let progress = 0;
    if (hasCurrentChallenge) {
        const startDate = new Date(savedChallenges.current.start);
        const finishDate = new Date(savedChallenges.current.finish);
        progress = calculateProgress(startDate, new Date(), finishDate);
    }

    const currentChallengeElement = hasCurrentChallenge
        ? <div>
            <CollapsibleContent title={savedChallenges.current.name}>
                <div className={"mb-2 pb-2 border-bottom"}>{savedChallenges.current.description}</div>
                <div className={"fw-bold"}>Start: {savedChallenges.current.start}</div>
                <div className={"fw-bold"}>Finish: {savedChallenges.current.finish}</div>
                <div className="progress my-2" role="progressbar" style={{height: "25px"}}
                     aria-label="Basic example" aria-valuenow={progress} aria-valuemin="0" aria-valuemax="100">
                    <div className="progress-bar btn-secondary" style={{width: progress + "%"}}>{progress + "%"}</div>
                </div>
            </CollapsibleContent>
        </div>
        : <div className={"my-2"}>No current challenge</div>;

    return (
        <Container>

            <Modal centered={true} show={showCreateChallenge} onHide={() => setShowCreateChallenge(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Create Sleep Challenge</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Button variant="secondary" className={"app-highlight w-100 mb-3"} onClick={selectPressed}>
                        Select from a list
                    </Button>
                    <form>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="challengeName" className="form-label">Short Name</label>
                            <input type="email" className="form-control" id="challengeName" placeholder=""
                                   value={challenge.name}
                                   onChange={e => updateChallenge({name: e.target.value})}/>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label type="text" htmlFor="description" className="form-label">Description</label>
                            <textarea rows="8" className="form-control" id="description" placeholder=""
                                      value={challenge.description}
                                      onChange={e => updateChallenge({description: e.target.value})}/>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="startDate" className="form-label">Start Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={date => updateChallenge({localStartTime: date})}
                                    selected={challenge.localStartTime}/>
                            </div>
                        </Container>
                        <Container className="ps-0 mb-3">
                            <label htmlFor="endDate" className="form-label">End Date</label>
                            <div>
                                <DatePicker
                                    className="form-control" id="startDate" dateFormat="MMMM d, yyyy"
                                    onChange={date => updateChallenge({localEndTime: date})}
                                    selected={challenge.localEndTime}/>
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

            <Modal centered={true} show={showPredefinedChallenges} onHide={() => {
                setShowPredefinedChallenges(false);
                setShowCreateChallenge(true);
            }}>
                <Modal.Header closeButton>
                    <Modal.Title>Predefined Challenges</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="secondary">
                        Select from a list here, you'll be able to edit it, and customize the name and dates.
                    </Alert>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => {
                        setShowPredefinedChallenges(false);
                        setShowCreateChallenge(true);
                    }}>Back To Create Challenge</Button>
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
                <Tabs defaultActiveKey="current" id="challenge-tabs">
                    <Tab eventKey="current" title="Current">
                        {currentChallengeElement}
                    </Tab>
                    <Tab eventKey="completed" title="Completed">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.completed.map((challenge, index) => {
                                return (
                                    <CollapsibleContent key={index} title={challenge.name}>
                                        <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
                                        <div className={"fw-bold"}>Start: {challenge.start}</div>
                                        <div className={"fw-bold"}>Finish: {challenge.finish}</div>
                                    </CollapsibleContent>
                                );
                            })}
                        </Container>
                    </Tab>
                    <Tab eventKey="upcoming" title="Future">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.upcoming.map((challenge, index) => {
                                return (
                                    <CollapsibleContent key={index} title={challenge.name}>
                                        <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
                                        <div className={"fw-bold"}>Start: {challenge.start}</div>
                                        <div className={"fw-bold"}>Finish: {challenge.finish}</div>
                                    </CollapsibleContent>
                                );
                            })}
                        </Container>
                    </Tab>
                </Tabs>
            </Container>

        </Container>
    );
}

export default Challenge;
