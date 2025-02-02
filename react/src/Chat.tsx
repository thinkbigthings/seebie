import React, {useEffect, useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import useHttpError from "./hooks/useHttpError";
import {useParams} from "react-router-dom";
import {fetchPostStr, GET} from "./utility/BasicHeaders.ts";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {mapToMessageDto, MessageDto, MessageType} from "./types/message.types.ts";


function Chat() {

    const {publicId} = useParams();
    if (publicId === undefined) {
        throw new Error("Public ID is required in the url");
    }

    const { throwOnHttpError } = useHttpError();

    const chatUrl = `/api/user/${publicId}/chat`

    const [messages, setMessages] = useState<MessageDto[]>([]);

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
    }, [messages]);

    const promptRef = useRef<HTMLTextAreaElement>(null);

    const appendMessage = (message:MessageDto) => {
        setMessages(prevChatHistory => [...prevChatHistory, message]);
    }

    const submitPrompt = () => {

        if(promptRef.current === null) {
            return;
        }

        const prompt = promptRef.current.value;
        promptRef.current.value = "";

        appendMessage({content: prompt, type: MessageType.USER});

        fetchPostStr(chatUrl, prompt)
            .then(throwOnHttpError)
            .then((response) => response.json())
            .then(appendMessage)
            .catch((error) => console.error('Error:', error));
    };

    useEffect(() => {
        fetch(chatUrl, GET)
            .then((response) => response.json() as Promise<MessageDto[]>)
            .then((data: any[]) => data.map(mapToMessageDto))
            .then(setMessages)
            .catch(error => console.log(error));
    }, []);

    return (
        <Container>
            <NavHeader title="Chat" />

            <Container className="mx-0 px-0">
                <Container id="chatHistory"
                           className="ps-0 border rounded"
                           style={{ height: '300px', overflowY: 'auto' }}
                            ref={chatHistoryRef}>
                    {
                        messages.map((message, i) => {
                            const alignment = message.type !== MessageType.USER ? "text-end" : "text-start";
                            return (
                                <Container key={i}>
                                    <Row className={"p-2 mb-1 pe-0"}>
                                        <Col className={alignment} >
                                            {message.content}
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