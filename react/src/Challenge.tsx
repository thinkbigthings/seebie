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

function Challenge() {

    const {publicId} = useParams();
    const callDelete = useApiDelete();

    // the user's current date is used to determine challenge completion status
    const challengeUrl = `/api/user/${publicId}/challenge`;

    const [deletedCount, setDeletedCount] = useState(0);


    // TODO Replace useDelete with TSQ mutation
    // update state when deleted
    const queryClient = useQueryClient();

    const deleteChallenge = (challengeId: number) => {
        const endpoint = `/api/user/${publicId}/challenge/${challengeId}`;
        callDelete(endpoint).then(() => setDeletedCount(deletedCount + 1));
    }

    // TODO ChallengeList doesn't need to be parameterized, maybe it needed to be in the past

    const fetchChallenges = () => fetch(challengeUrl, GET)
        .then((response) => response.json() as Promise<ChallengeDetailDto[]>);


    // TODO update state when created
    // passing saved challenges to CreateChallenge is really only used for prop drilling
    // to do validation on new names to prevent name collisions

    const { data: savedChallenges = emptyChallengeList } = useQuery<ChallengeDetailDto[], Error, ChallengeList<ChallengeData>>({
        queryKey: [challengeUrl],
        queryFn: fetchChallenges,
        placeholderData: [] as ChallengeDetailDto[],
        staleTime: Infinity,
        select: (data: ChallengeDetailDto[]) => toChallengeList(data),
    });

    return (
        <Container>

            <NavHeader title="Sleep Challenge">
                <CreateChallenge onCreated={() => {}}
                                 savedChallenges={[...savedChallenges.completed, ...savedChallenges.current, ...savedChallenges.upcoming]}
                />
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
