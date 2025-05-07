import React, {useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";
import {ChallengeDetailDto, ChallengeDto} from "./types/challenge.types.ts";
import {useMutation, useQueryClient, useSuspenseQuery} from "@tanstack/react-query";
import {ensure, toChallengeDto, toLocalChallengeData} from "./utility/Mapper.ts";
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App.tsx";
import WarningButton from "./component/WarningButton.tsx";
import ChallengeForm from "./ChallengeForm.tsx";
import Button from 'react-bootstrap/esm/Button';
import {httpDelete, httpGet, httpPut, UploadVars} from "./utility/apiClient.ts";

function EditChallenge() {

    let {publicId, challengeId} = useParams();

    const numericChallengeId = parseInt(ensure(challengeId), 10);

    const navigate = useNavigate();

    const challengeUrl = `/api/user/${publicId}/challenge`;
    const challengeDetailUrl = `${challengeUrl}/${numericChallengeId}`;

    const {data} = useSuspenseQuery<ChallengeDetailDto>({
        queryKey: [challengeDetailUrl],
        queryFn: () => httpGet<ChallengeDetailDto>(challengeDetailUrl)
    });

    const [draftChallenge, setDraftChallenge] = useState(toLocalChallengeData(data));

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
                return updatedChallenge;
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
            queryClient.invalidateQueries({queryKey: [response.url]});

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
                    <ChallengeForm challengeUrl={challengeUrl}
                                   draftChallenge={draftChallenge}
                                   onValidityChanged={setDataValid}
                                   onChallengeChanged={setDraftChallenge} />
                </Container>

                <div className="d-flex flex-row">
                    <Button className="me-3" variant="primary" onClick={onSave} disabled={!dataValid} >Save</Button>
                    <Button variant="secondary" onClick={() => navigate(-1)}>Cancel</Button>
                </div>

            </Container>
        </>
    );
}

export default EditChallenge;
