import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import {useParams} from "react-router-dom";
import {Tab, Tabs} from "react-bootstrap";
import { emptyChallengeList} from "./utility/Constants";
import CollapsibleChallenge from "./component/CollapsibleChallenge";
import useApiDelete from "./hooks/useApiDelete";
import CreateChallenge from "./CreateChallenge";
import {useQueryClient} from "@tanstack/react-query";
import {useChallenges} from "./hooks/useChallenges.ts";

function Challenge() {

    const {publicId} = useParams();

    // the user's current date is used to determine challenge completion status
    const challengeUrl = `/api/user/${publicId}/challenge`;


    // TODO Replace useDelete with TSQ mutation, update state when deleted
    // try httpDelete(), compare with useApiDelete()
    // can we selectively remove from the TSQ cache? or should we invalidate the whole challenge cache?

    const queryClient = useQueryClient();
    const [deletedCount, setDeletedCount] = useState(0);
    const callDelete = useApiDelete();
    const deleteChallenge = (challengeId: number) => {
        const endpoint = `${challengeUrl}/${challengeId}`;
        callDelete(endpoint).then(() => setDeletedCount(deletedCount + 1));
    }

    // TODO ChallengeList doesn't need to be parameterized, maybe it needed to be in the past

    const { data: savedChallenges = emptyChallengeList } = useChallenges(challengeUrl);

    return (
        <Container>

            <NavHeader title="Sleep Challenge">
                <CreateChallenge challengeUrl={challengeUrl} />
            </NavHeader>

            <Container className="container mt-3 px-0">
                <Tabs defaultActiveKey="current" id="challenge-tabs">
                    <Tab eventKey="current" title="Current">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.current.map((challenge, index) =>
                                <CollapsibleChallenge key={index} challenge={challenge}
                                                      onDelete={() => deleteChallenge(challenge.id)} />
                            )}
                        </Container>
                    </Tab>
                    <Tab eventKey="completed" title="Completed">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.completed.map((challenge, index) =>
                                <CollapsibleChallenge key={index} challenge={challenge}
                                                      onDelete={() => deleteChallenge(challenge.id)} />
                            )}
                        </Container>
                    </Tab>
                    <Tab eventKey="upcoming" title="Future">
                        <Container className="px-0 overflow-y-scroll h-70vh ">
                            {savedChallenges.upcoming.map((challenge, index) =>
                                <CollapsibleChallenge key={index} challenge={challenge}
                                                      onDelete={() => deleteChallenge(challenge.id)} />
                            )}
                        </Container>
                    </Tab>
                </Tabs>
            </Container>

        </Container>
    );
}

export default Challenge;
