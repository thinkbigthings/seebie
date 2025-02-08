import React, {useEffect, useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import useHttpError from "./hooks/useHttpError";
import {useParams} from "react-router-dom";
import {fetchPost, GET} from "./utility/BasicHeaders.ts";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {mapToMessageDto, MessageDto, MessageType} from "./types/message.types.ts";
import {faCircle, faMoon} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import CollapsibleContent from "./component/CollapsibleContent.tsx";


function Chat() {

    const {publicId} = useParams();
    if (publicId === undefined) {
        throw new Error("Public ID is required in the url");
    }

    const { throwOnHttpError } = useHttpError();

    const chatUrl = `/api/user/${publicId}/chat`

    const [messages, setMessages] = useState<MessageDto[]>([]);
    const [processing, setProcessing] = useState<boolean>(false);

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
        setProcessing(message.type === MessageType.USER);
        setMessages(prevChatHistory => [...prevChatHistory, message]);
    }

    const submitPrompt = () => {

        setProcessing(true);

        if (promptRef.current === null) {
            return;
        }

        const prompt = promptRef.current.value;
        promptRef.current.value = "";

        const newUserPrompt = {content: prompt.trim(), type: MessageType.USER};
        appendMessage(newUserPrompt);

        fetchPost(chatUrl, newUserPrompt)
            .then(throwOnHttpError)
            .then(response => response.json() as Promise<MessageDto>)
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

    const userRowStyle = "ms-5 chat-bot";
    const botRowStyle = "me-5 chat-user";

    return (
        <Container className={"p-0"}
            style={{
                height: '90vh',
                display: 'flex',
                flexDirection: 'column',
                overflow: 'hidden',
            }}
        >
            <NavHeader title="Chat" />
            {/* Use a div instead of Container if Bootstrap's default margins/paddings interfere */}
            <div
                className="mx-0 px-0 d-flex flex-column"
                style={{ flex: 1, overflow: 'hidden' }}
            >
                <div
                    id="chatHistory"
                    className="border rounded m-0 p-1"
                    style={{ flex: 1, minHeight: 0, overflowY: 'auto' }}
                    ref={chatHistoryRef}
                >
                    {messages.map((message, i) => {
                        const rowStyle = message.type === MessageType.USER ? userRowStyle : botRowStyle;
                        return (
                            <Container key={i}>
                                <Row className={`${rowStyle} border rounded mt-1 `}>
                                    <Col>{message.content}</Col>
                                </Row>
                            </Container>
                        );
                    })}
                    {processing && (
                        <Container key="processing">
                            <Row>
                                <Col className="text-start">
                                    <FontAwesomeIcon className="fa-beat-fade ms-2" icon={faCircle} />
                                </Col>
                            </Row>
                        </Container>
                    )}
                </div>

                <textarea
                    className="form-control"
                    id="prompt"
                    placeholder="Press Enter to send"
                    rows={3}
                    ref={promptRef}
                    onKeyUp={(e) => callOnEnter(e, submitPrompt)}
                />
            </div>
        </Container>
    );
}

export default Chat;