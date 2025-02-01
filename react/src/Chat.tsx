import React, {useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import useHttpError from "./hooks/useHttpError";


function Chat() {

    const { throwOnHttpError } = useHttpError();

    const [prompt, setPrompt] = useState("");
    const [chatHistory, setChatHistory] = useState([]);

    // const handleSubmission = () => {
    //
    //     fetch(apiUrl)
    //         .then(throwOnHttpError)
    //         .then((response) => response.json())
    //         .then(console.log)
    //         .catch((error) => console.error('Error:', error));
    // };

    return (
        <Container>
            <NavHeader title="Chat" />

            <Container className="mx-0 px-0 py-2 border-top border-light-subtle">
                <Container className="ps-0">
                    <textarea className="form-control mt-2"
                              id="chatHistory"
                              placeholder="Nothing to show yet"
                              rows={20}
                              disabled={true}
                    />
                    <textarea className="form-control mt-2"
                              id="prompt"
                              placeholder="prompt"
                              rows={3}
                              defaultValue=""
                    />
                </Container>
            </Container>
        </Container>
    );
}

export default Chat;