import React, {useEffect, useState} from 'react';

import Container from "react-bootstrap/Container";
import useApiPut from "./hooks/useApiPut";
import {GET} from "./utility/BasicHeaders";
import Button from "react-bootstrap/Button";
import useApiDelete from "./hooks/useApiDelete";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";
import WarningButton from "./component/WarningButton";
import ChallengeForm from "./ChallengeForm";
import {emptyChallengeList, emptyEditableChallenge} from "./utility/Constants";
import {fromChallengeDto, mapChallengeDetails, toChallengeDto, withExactTime} from "./utility/Mapper";

const removeDetailsWithId = (challengeList, challengeId) => {
    return {
        current: challengeList.current.filter(details => details.id !== challengeId),
        upcoming: challengeList.upcoming.filter(details => details.id !== challengeId),
        completed: challengeList.completed.filter(details => details.id !== challengeId)
    }
}


function EditChallenge() {

    const navigate = useNavigate();

    const {username, challengeId} = useParams();
    const numericChallengeId = +challengeId; // Converts string to number

    const challengeEndpoint = `/api/user/${username}/challenge/${challengeId}`;
    const tz = encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
    const challengeEndpointTz = `/api/user/${username}/challenge?zoneId=${tz}`;

    const [loaded, setLoaded] = useState(false);
    const [editableChallenge, setEditableChallenge] = useState(emptyEditableChallenge());
    const [dataValid, setDataValid] = useState(true);
    const [savedChallenges, setSavedChallenges] = useState(emptyChallengeList);

    const put = useApiPut();
    const callDelete = useApiDelete();


    useEffect(() => {
        fetch(challengeEndpoint, GET)
            .then(response => response.json())
            .then(fromChallengeDto)
            .then(setEditableChallenge)
            .then(() => setLoaded(true))
    }, [setEditableChallenge, challengeEndpoint]);

    useEffect(() => {
        fetch(challengeEndpointTz, GET)
            .then((response) => response.json())
            .then(challengeList => mapChallengeDetails(challengeList, withExactTime))
            .then(challengeList => removeDetailsWithId(challengeList, numericChallengeId))
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [challengeEndpointTz]);

    const onSave = () => {
        put(challengeEndpoint, toChallengeDto(editableChallenge)).then(() => navigate(-1));
    }

    const deleteById = () => {
        callDelete(challengeEndpoint).then(() => navigate(-1));
    }

    return (
        <Container>

            <NavHeader title="Sleep Details">
                <WarningButton buttonText="Delete" onConfirm={deleteById}>
                    This deletes the current sleep challenge and cannot be undone. Proceed?
                </WarningButton>
            </NavHeader>

            <Container id="challengeFormWrapper" className="px-0">
                {loaded
                    ? <ChallengeForm editableChallenge={editableChallenge}
                                    setEditableChallenge={setEditableChallenge}
                                    setDataValid={setDataValid}
                                    savedChallenges={savedChallenges} />
                    : <div /> }
            </Container>

            <div className="d-flex flex-row">
                <Button className="me-3" variant="primary" onClick={onSave} disabled={ ! dataValid} >Save</Button>
                <Button  variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
            </div>

        </Container>
    );
}

export default EditChallenge;
