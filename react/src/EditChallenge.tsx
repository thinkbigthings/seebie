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
import {emptyChallengeDataArray, emptyEditableChallenge} from "./utility/Constants";
import { toLocalChallengeData, toChallengeDto, toLocalChallengeDataList, toChallengeDetailDto} from "./utility/Mapper";
import {ChallengeDetailDto, ChallengeDto} from "./types/challenge.types";

const removeChallengesWithId = (challengeList: ChallengeDetailDto[], challengeId: number) => {
    return challengeList.filter(details => details.id !== challengeId);
}


function EditChallenge() {

    const navigate = useNavigate();

    const {publicId, challengeId} = useParams();

    if (challengeId === undefined) {
        throw new Error("Challenge ID is required.");
    }

    const numericChallengeId = parseInt(challengeId);

    const allChallengesEndpoint = `/api/user/${publicId}/challenge`;
    const challengeEndpoint = `/api/user/${publicId}/challenge/${challengeId}`;

    const [loaded, setLoaded] = useState(false);
    const [editableChallenge, setEditableChallenge] = useState(emptyEditableChallenge());
    const [dataValid, setDataValid] = useState(true);
    const [savedChallenges, setSavedChallenges] = useState(emptyChallengeDataArray);

    const put = useApiPut();
    const callDelete = useApiDelete();


    useEffect(() => {
        fetch(challengeEndpoint, GET)
            .then(response => response.json() as Promise<ChallengeDto>)
            .then(challenge => toChallengeDetailDto(challenge, numericChallengeId))
            .then(toLocalChallengeData)
            .then(setEditableChallenge)
            .then(() => setLoaded(true))
    }, [setEditableChallenge, challengeEndpoint]);

    // load all challenges to check for validation
    useEffect(() => {
        fetch(allChallengesEndpoint, GET)
            .then((response) => response.json() as Promise<ChallengeDetailDto[]>)
            .then(challengeList => removeChallengesWithId(challengeList, numericChallengeId))
            .then(toLocalChallengeDataList)
            .then(setSavedChallenges)
            .catch(error => console.log(error));
    }, [allChallengesEndpoint]);

    const onSave = () => {
        put(challengeEndpoint, toChallengeDto(editableChallenge)).then(() => navigate(-1));
    }

    const deleteById = () => {
        callDelete(challengeEndpoint).then(() => navigate(-1));
    }

    return (
        <Container>

            <NavHeader title="Challenge">
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
