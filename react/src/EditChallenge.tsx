import React, {useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import {GET} from "./utility/BasicHeaders.ts";
import {ChallengeData, ChallengeDetailDto, ChallengeDto} from "./types/challenge.types.ts";
import {useMutation, useQueryClient, useSuspenseQuery} from "@tanstack/react-query";
import {toChallengeDetailDto, toChallengeDto, toLocalChallengeData} from "./utility/Mapper.ts";
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App.tsx";
import WarningButton from "./component/WarningButton.tsx";
import ChallengeFormTSQ from "./ChallengeFormTSQ.tsx";
import Button from 'react-bootstrap/esm/Button';
import {httpDelete, httpPut, UploadVars} from "./utility/apiClient.ts";

function ensure<T>(argument: T | undefined | null, message: string = 'This value was promised to be there.'): T {
    if (argument === undefined || argument === null) {
        throw new TypeError(message);
    }
    return argument as T;
}

function EditChallenge() {

    let {publicId, challengeId} = useParams();

    const numericChallengeId = parseInt(ensure(challengeId));

    const navigate = useNavigate();

    const challengeUrl = `/api/user/${publicId}/challenge`;
    const challengeDetailUrl = `${challengeUrl}/${numericChallengeId}`;

    const fetchChallenges = () => fetch(challengeUrl, GET)
        .then((response) => response.json() as Promise<ChallengeDetailDto[]>);

    const {data: savedChallenges} = useSuspenseQuery<ChallengeDetailDto[]>({
        queryKey: [challengeUrl],
        queryFn: fetchChallenges
    });

    const validationChallenges = savedChallenges
        .filter(challenge => challenge.id !== numericChallengeId)
        .map(challenge => toLocalChallengeData(challenge));

    const fetchChallenge = () => fetch(challengeDetailUrl, GET)
        .then((response) => response.json() as Promise<ChallengeDto>);

    const {data} = useSuspenseQuery<ChallengeDto>({
        queryKey: [challengeDetailUrl],
        queryFn: fetchChallenge
    });


    let loadedChallenge = toLocalChallengeData(toChallengeDetailDto(data, numericChallengeId));
    const [draftChallenge, setDraftChallenge] = useState<ChallengeData>(loadedChallenge);

    const [dataValid, setDataValid] = useState(true);

    const queryClient = useQueryClient();

    const updateChallenge = useMutation({
        mutationFn: (vars: UploadVars<ChallengeDto>) => httpPut<ChallengeDto,ChallengeDetailDto>(vars.url, vars.body),
        onSuccess: (updatedChallenge: ChallengeDetailDto) => {

            queryClient.setQueryData([challengeUrl], (oldData: ChallengeDetailDto[]) => {
                const updatedList = oldData.filter(challenge => challenge.id !== updatedChallenge.id);
                return [ ...(updatedList ?? []), updatedChallenge ]
            });

            queryClient.setQueryData([challengeDetailUrl], (oldData: ChallengeDetailDto) => {
                return toChallengeDto(draftChallenge);
            })

            navigate(-1);
        },
    });

    const onSave = () => {
        updateChallenge.mutate({
            url: challengeDetailUrl,
            body: toChallengeDto(draftChallenge)
        });
    }


    const deleteChallenge = useMutation({
        mutationFn: (url: string) => httpDelete(url),
        onSuccess: (response) => {

            // remove the deleted challenge from the list of challenges
            queryClient.setQueryData([challengeUrl], (oldData: ChallengeDetailDto[]) => {
                return oldData.filter(challenge => challenge.id !== numericChallengeId);
            });

            // remove the delete challenge from the specific challenge detail cache
            queryClient.invalidateQueries({queryKey: [response.url]})
                .then(()=>{});

            navigate(-1);
        },
    });

    const deleteById = () => {
        deleteChallenge.mutate(challengeDetailUrl);
    }

    return (
        <>
            <Container>

                <NavHeader title="Challenge">
                    <WarningButton buttonText="Delete" onConfirm={deleteById}>
                        This deletes the current sleep challenge and cannot be undone. Proceed?
                    </WarningButton>
                </NavHeader>

                <Container id="challengeFormWrapper" className="px-0">
                    <ChallengeFormTSQ savedChallenges={validationChallenges}
                                      draftChallenge={draftChallenge}
                                      onValidityChanged={setDataValid}
                                      onChallengeChanged={setDraftChallenge} />

                </Container>

                <div className="d-flex flex-row">
                    <Button className="me-3" variant="primary" onClick={onSave} disabled={ ! dataValid} >Save</Button>
                    <Button  variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
                </div>

            </Container>
        </>
    );
}

export default EditChallenge;
