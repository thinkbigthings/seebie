import React from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import {useParams} from "react-router-dom";
import {Tab, Tabs} from "react-bootstrap";
import CollapsibleChallenge from "./component/CollapsibleChallenge";
import CreateChallenge from "./CreateChallenge";
import {ChallengeDetailDto, ChallengeList} from "./types/challenge.types.ts";
import {useMutation, useQueryClient, useSuspenseQuery} from "@tanstack/react-query";
import {toChallengeList} from "./utility/Mapper.ts";
import {httpDelete, httpGet} from "./utility/apiClient.ts";

function Challenge() {

    const {publicId} = useParams();

    const challengeUrl = `/api/user/${publicId}/challenge`;

    const queryClient = useQueryClient();

    const deleteMutation = useMutation({
        mutationFn: (url: string) => httpDelete(url),
        onSuccess: () => queryClient.invalidateQueries({queryKey: [challengeUrl]})
    });

    const deleteChallenge = (challengeId: number) => {
        deleteMutation.mutate(`${challengeUrl}/${challengeId}`);
    }

    const {data: savedChallenges} = useSuspenseQuery<ChallengeDetailDto[], Error, ChallengeList>({
        queryKey: [challengeUrl],
        queryFn: () => httpGet<ChallengeDetailDto[]>(challengeUrl),
        select: toChallengeList
    });

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
