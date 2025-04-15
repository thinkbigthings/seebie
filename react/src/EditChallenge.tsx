import React, {useState} from 'react';

import Container from "react-bootstrap/Container";
import useApiPut from "./hooks/useApiPut";
import Button from "react-bootstrap/Button";
import useApiDelete from "./hooks/useApiDelete";
import {NavHeader} from "./App";
import {useNavigate, useParams} from "react-router-dom";
import WarningButton from "./component/WarningButton";
import ChallengeForm from "./ChallengeForm";
import {emptyChallengeList} from "./utility/Constants";
import {flatten, toChallengeDto,} from "./utility/Mapper";
import {useChallenges} from "./hooks/useChallenges.ts";

function ensure<T>(argument: T | undefined | null, message: string = 'This value was promised to be there.'): T {
    if (argument === undefined || argument === null) {
        throw new TypeError(message);
    }
    return argument;
}

function EditChallenge() {

    const {publicId, challengeId} = useParams();
    const navigate = useNavigate();

    if (challengeId === undefined) {
        throw new Error("Challenge ID is required.");
    }

    const numericChallengeId = parseInt(challengeId);

    const challengeUrl = `/api/user/${publicId}/challenge`;
    const editChallengeUrl = `${challengeUrl}/${challengeId}`;

    const [dataValid, setDataValid] = useState(true);

    // TODO pass in the TSQ key challenge url instead of the list
    const { data: savedChallenges = emptyChallengeList } = useChallenges(challengeUrl);
    const allChallenges = flatten(savedChallenges);
    const maybeChallenge = allChallenges.find(challenge => challenge.id === numericChallengeId);
    const existingChallenge = ensure(maybeChallenge);

    const [editableChallenge, setEditableChallenge] = useState(existingChallenge);

    // TODO try httpPut, httpDelete
    // use mutation, can we selectively update the cache?

    const put = useApiPut();
    const callDelete = useApiDelete();

    const onSave = () => {
        put(editChallengeUrl, toChallengeDto(editableChallenge)).then(() => navigate(-1));
    }

    const deleteById = () => {
        callDelete(editChallengeUrl).then(() => navigate(-1));
    }

    return (
        <Container>

            <NavHeader title="Challenge">
                <WarningButton buttonText="Delete" onConfirm={deleteById}>
                    This deletes the current sleep challenge and cannot be undone. Proceed?
                </WarningButton>
            </NavHeader>

            <Container id="challengeFormWrapper" className="px-0">
                <ChallengeForm editableChallenge={editableChallenge}
                                setEditableChallenge={setEditableChallenge}
                                setDataValid={setDataValid}
                                savedChallenges={savedChallenges} />
            </Container>

            <div className="d-flex flex-row">
                <Button className="me-3" variant="primary" onClick={onSave} disabled={ ! dataValid} >Save</Button>
                <Button  variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
            </div>

        </Container>
    );
}

export default EditChallenge;
