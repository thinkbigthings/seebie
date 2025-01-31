import React, {useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faKey} from "@fortawesome/free-solid-svg-icons";
import useHttpError from "./hooks/useHttpError";
import Modal from "react-bootstrap/Modal";

function Chat() {

    const { throwOnHttpError } = useHttpError();

    const [apiKey, setApiKey] = useState("");
    const [prompt, setPrompt] = useState("");
    const [chatHistory, setChatHistory] = useState("");
    const [showApiKeyInput, setShowApiKeyInput] = useState(false);
    const apiUrl = "https://api.openai.com";

    const apiKeyRef = useRef<HTMLTextAreaElement>(null); // Create a ref for the textarea
    const handleSetApiKey = () => {
        if (apiKeyRef.current) {
            setApiKey(apiKeyRef.current.value); // Update state only when the button is clicked
        }
        setShowApiKeyInput(false);
    };

    const promptRef = useRef<HTMLTextAreaElement>(null); // Create a ref for the textarea
    const handleSetPrompt = () => {
        if (promptRef.current) {
            setPrompt(promptRef.current.value); // Update state only when the button is clicked
        }
    };

    const isApiKeySet = apiKey !== "";
    const buttonText = isApiKeySet ? "See API Key" : "Set API Key";

    const handleSubmission = () => {

        fetch(apiUrl)
            .then(throwOnHttpError)
            .then((response) => response.json())
            .then(console.log)
            .catch((error) => console.error('Error:', error));
    };

    return (
        <Container>
            <Modal centered={true} show={showApiKeyInput} onHide={() => setShowApiKeyInput(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>API Key</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <form>

                        <Container className="ps-0 mb-3">
                            <textarea className="form-control"
                                      id="inputDisplayName"
                                      placeholder="API Key"
                                      rows={6}
                                      defaultValue={apiKey}
                                      ref={apiKeyRef}
                            />
                        </Container>

                        <div className="d-flex flex-row">
                            <Button className="me-3" variant="primary" onClick={handleSetApiKey} >Set</Button>
                            <Button className="" variant="secondary" onClick={() => setShowApiKeyInput(false)}>Cancel</Button>
                        </div>

                    </form>
                </Modal.Body>
            </Modal>
            <NavHeader title="Chat">
                <Button variant={isApiKeySet ? "success" : "danger"}
                        onClick={ () => setShowApiKeyInput(true) } >
                    <FontAwesomeIcon className="me-2" icon={faKey} />
                    {buttonText}
                </Button>
            </NavHeader>

            <Container className="mx-0 px-0 py-2 border-top border-light-subtle">
                <Container className="ps-0">
                    <textarea className="form-control mt-2"
                              id="chatHistory"
                              placeholder="Nothing to show yet"
                              rows={20}
                              disabled={true}
                    />
                    <textarea className="form-control mt-2" id="prompt" placeholder="prompt"
                              rows={3}
                           defaultValue=""
                           ref={promptRef}
                    />
                </Container>
            </Container>
        </Container>
    );
}

export default Chat;