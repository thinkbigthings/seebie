import React, {useEffect, useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import useHttpError from "./hooks/useHttpError";
import {useParams} from "react-router-dom";
import {fetchPostStr, GET} from "./utility/BasicHeaders.ts";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

interface PromptResponse {
    prompt: string,
    response: string
}

function Chat() {

    const {publicId} = useParams();
    if (publicId === undefined) {
        throw new Error("Public ID is required in the url");
    }

    const { throwOnHttpError } = useHttpError();

    const chatUrl = `/api/user/${publicId}/chat`

    const [chatHistory, setChatHistory] = useState<PromptResponse[]>([]);

    const chatHistoryRef = useRef<HTMLDivElement>(null);



    // call the callback function if the enter key was pressed in the event
    function callOnEnter(event:React.KeyboardEvent<HTMLTextAreaElement>, callback:()=>void) {
        if(event.key === 'Enter') {
            callback();
        }
    }

    // Auto-scroll to bottom whenever chatHistory updates
    useEffect(() => {
        if (chatHistoryRef.current) {
            chatHistoryRef.current.scrollTop = chatHistoryRef.current.scrollHeight;
        }
    }, [chatHistory]);

    const promptRef = useRef<HTMLTextAreaElement>(null);

    const appendChatHistory = (response:PromptResponse) => {
        setChatHistory(prevChatHistory => [...prevChatHistory, response]);
    }

    const submitPrompt = () => {

        if(promptRef.current === null) {
            return;
        }

        const prompt = promptRef.current.value;
        promptRef.current.value = "";

        fetchPostStr(chatUrl, prompt)
            .then(throwOnHttpError)
            .then((response) => response.json())
            .then(appendChatHistory)
            .catch((error) => console.error('Error:', error));
    };


    return (
        <Container>
            <NavHeader title="Chat" />

            <Container className="mx-0 px-0">
                <Container id="chatHistory"
                           className="ps-0 border rounded"
                           style={{ height: '300px', overflowY: 'auto' }}
                            ref={chatHistoryRef}>
                    {
                        chatHistory.map((promptResponse, i) => {
                            return (
                                <Container key={i}>
                                    <Row className={"p-2 mb-1 pe-0"}>
                                        <Col className="text-end">
                                            {promptResponse.prompt}
                                        </Col>
                                    </Row>
                                    <Row className={"p-2 mb-1 pe-0"}>
                                        <Col className="text-start">
                                            {promptResponse.response}
                                        </Col>
                                    </Row>
                                </Container>
                            )
                        })
                    }
                </Container>

                <textarea className="form-control mt-2"
                          id="prompt"
                          placeholder="prompt"
                          rows={3}
                          ref={promptRef}
                          onKeyUp={e => callOnEnter(e, submitPrompt)}
                />

            </Container>
        </Container>
    );
}

export default Chat;