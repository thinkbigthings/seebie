import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPlus} from "@fortawesome/free-solid-svg-icons";
import Modal from "react-bootstrap/Modal";
import Alert from "react-bootstrap/Alert";
import CollapsibleContent from "./component/CollapsibleContent";
import {emptyEditableChallenge, NameDescription, PREDEFINED_CHALLENGES} from "./utility/Constants";
import SuccessModal from "./component/SuccessModal";
import {toChallengeDto} from "./utility/Mapper";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {ChallengeDetailDto, ChallengeDto} from "./types/challenge.types.ts";
import {httpPost, UploadVars} from "./utility/apiClient.ts";
import ChallengeForm from "./ChallengeForm.tsx";

interface CreateChallengeProps { challengeUrl: string }

function CreateChallenge(props:CreateChallengeProps) {

    const {challengeUrl} = props;

    const [showCreateSuccess, setShowCreateSuccess] = useState(false);
    const [showCreateChallenge, setShowCreateChallenge] = useState(false);
    const [showPredefinedChallenges, setShowPredefinedChallenges] = useState(false);
    const [draftChallenge, setDraftChallenge] = useState(emptyEditableChallenge());

    // validation of the overall form, so we know whether to enable the save button
    const [dataValid, setDataValid] = useState(true);

    const clearChallengeEdit = () => {
        setShowCreateChallenge(false);
        setDraftChallenge(emptyEditableChallenge());
        setDataValid(true);
    }

    const onSelectChallenge = (selectedChallenge: NameDescription) => {
        let updatedChallenge = {...draftChallenge};
        updatedChallenge.name = selectedChallenge.name;
        updatedChallenge.description = selectedChallenge.description;
        setDraftChallenge(updatedChallenge);
        swapModals();
    }

    const swapModals = () => {
        setShowCreateChallenge( ! showCreateChallenge);
        setShowPredefinedChallenges( ! showPredefinedChallenges);
    }

    const queryClient = useQueryClient();

    const uploadNewChallenge = useMutation({
        mutationFn: (vars: UploadVars<ChallengeDto>) => httpPost<ChallengeDto,ChallengeDetailDto>(vars.url, vars.body),
        onSuccess: (newlyCreatedChallenge: ChallengeDetailDto) => {
            setShowCreateSuccess(true);
            clearChallengeEdit();
            queryClient.setQueryData([challengeUrl], (oldData: ChallengeDetailDto[]) => {
                return [ ...(oldData ?? []), newlyCreatedChallenge ];
            });
        }
    });

    const saveEditableChallenge = () => {
        uploadNewChallenge.mutate({
            url: challengeUrl,
            body: toChallengeDto(draftChallenge)
        });
    };

    return (
        <>
            <Button variant="success" disabled={false} onClick={() => setShowCreateChallenge(true)}>
                <FontAwesomeIcon icon={faPlus}/>
            </Button>

            <Modal centered={true} show={showCreateChallenge} onHide={clearChallengeEdit}>
                <Modal.Header closeButton>
                    <Modal.Title>Create Sleep Challenge</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Button variant="secondary" className={"app-highlight w-100 mb-3"} onClick={swapModals}>
                        Select from a list
                    </Button>
                    <ChallengeForm challengeUrl={challengeUrl}
                                   draftChallenge={draftChallenge}
                                   onValidityChanged={setDataValid}
                                   onChallengeChanged={setDraftChallenge} />
                </Modal.Body>
                <Modal.Footer>
                    <div className="d-flex flex-row">
                        <Button className="me-3" variant="success" onClick={saveEditableChallenge}
                                disabled={!dataValid}>Save</Button>
                        <Button className="" variant="secondary" onClick={clearChallengeEdit}>Cancel</Button>
                    </div>
                </Modal.Footer>
            </Modal>

            <Modal centered={true} show={showPredefinedChallenges} onHide={swapModals}>
                <Modal.Header closeButton>
                    <Modal.Title>Predefined Challenges</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Alert variant="secondary">
                        Select from a pre-defined list here. Once selected, you'll be able to edit it and customize the name and dates.
                    </Alert>
                    <Container className="px-0 overflow-y-scroll h-50vh ">
                        {PREDEFINED_CHALLENGES.map((challenge, index) => {
                            return (
                                <CollapsibleContent key={index} title={challenge.name}>
                                    <div className={"mb-2 pb-2 border-bottom"}>{challenge.description}</div>
                                    <Button variant="success" className="mt-2 w-100" onClick={ () => onSelectChallenge(challenge)}>
                                        Select
                                    </Button>
                                </CollapsibleContent>
                            );
                        })}
                        </Container>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={swapModals}>
                        Back To Create Challenge
                    </Button>
                </Modal.Footer>
            </Modal>

            <SuccessModal title="Creation Success" showing={showCreateSuccess} handleClose={() => setShowCreateSuccess(false)}>
                Challenge created successfully! It will appear in your list of challenges.
            </SuccessModal>

        </>
    );
}

export default CreateChallenge;
