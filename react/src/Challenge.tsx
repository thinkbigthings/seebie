import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import {useParams} from "react-router-dom";
import {GET} from "./utility/BasicHeaders";
import {Tab, Tabs} from "react-bootstrap";
import { emptyChallengeList} from "./utility/Constants";
import CollapsibleChallenge from "./component/CollapsibleChallenge";
import useApiDelete from "./hooks/useApiDelete";
import CreateChallenge from "./CreateChallenge";
import {toChallengeList} from "./utility/Mapper";
import {ChallengeData, ChallengeDetailDto, ChallengeList} from "./types/challenge.types";
import {useQuery, useQueryClient} from "@tanstack/react-query";
import {useChallenges} from "./hooks/useChallenges.ts";

function Challenge() {

    const {publicId} = useParams();
    const callDelete = useApiDelete();

    // the user's current date is used to determine challenge completion status
    const challengeUrl = `/api/user/${publicId}/challenge`;



    // TODO Replace useDelete with TSQ mutation
    // update state when deleted
    const queryClient = useQueryClient();
    const [deletedCount, setDeletedCount] = useState(0);
    const deleteChallenge = (challengeId: number) => {
        const endpoint = `/api/user/${publicId}/challenge/${challengeId}`;
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
