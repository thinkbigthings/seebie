import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import Button from "react-bootstrap/Button";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faDownload, faKey, faPlus, faUpload} from "@fortawesome/free-solid-svg-icons";
import Form from 'react-bootstrap/Form';
import useHttpError from "./hooks/useHttpError";
import {useApiGet} from "./hooks/useApiGet";
import {useParams} from "react-router-dom";
import SuccessModal from "./component/SuccessModal";
import Modal from "react-bootstrap/Modal";

function Chat() {

    const { publicId } = useParams();
    const { throwOnHttpError } = useHttpError();

    const [apiKey, setApiKey] = useState("");
    const [showApiKeyInput, setShowApiKeyInput] = useState(false);
    const apiUrl = "https://api.openai.com";

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
                <h4 className="mb-3">Chat</h4>

            </Container>
        </Container>
    );
}

export default Chat;