import React, {useCallback, useEffect, useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import useHttpError from "./hooks/useHttpError";
import {useParams} from "react-router-dom";
import {fetchPost, GET} from "./utility/BasicHeaders.ts";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {mapToMessageDto, MessageDto, MessageType} from "./types/message.types.ts";
import {faCircle} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InfoModalButton from "./component/InfoModalButton.tsx";


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
        const chatDiv = chatHistoryRef.current;
        if (!chatDiv) return;

        const isAtBottom = chatDiv.scrollHeight - chatDiv.clientHeight <= chatDiv.scrollTop + 10;
        if (isAtBottom) {
            chatDiv.scrollTop = chatDiv.scrollHeight;
        }
    }, [messages]);

    const promptRef = useRef<HTMLTextAreaElement>(null);

    const appendMessage = (message:MessageDto) => {
        setProcessing(message.type === MessageType.USER);
        setMessages(prevChatHistory => [...prevChatHistory, message]);
    }

    const submitPrompt = () => {
        setProcessing(true);
        const prompt = handleUserInput();
        if (!prompt) return;

        const newUserPrompt: MessageDto = { content: prompt, type: MessageType.USER };
        appendMessage(newUserPrompt);

        sendMessageToServer(newUserPrompt)
            .catch(error => console.error("Failed to send message:", error));
    };

    const handleUserInput = (): string | null => {
        if (!promptRef.current) return null;
        const prompt = promptRef.current.value.trim();
        promptRef.current.value = "";
        return prompt;
    };

    const sendMessageToServer = async (message: MessageDto) => {
        try {
            const response = await fetchPost(chatUrl, message);
            throwOnHttpError(response);
            const data = await response.json() as MessageDto;
            appendMessage(data);
        } catch (error) {
            console.error("Error:", error);
        }
    };

    useEffect(() => {
        const abortController = new AbortController();
        fetch(chatUrl, { ...GET, signal: abortController.signal })
            .then(response => response.json() as Promise<MessageDto[]>)
            .then(data => setMessages(data.map(mapToMessageDto)))
            .catch(error => {
                if (error.name !== "AbortError") console.log(error);
            });

        return () => abortController.abort();
    }, []);

    const handleKeyUp = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        callOnEnter(e, submitPrompt);
    }, []);

    const userRowStyle = "chat-message-user text-start";
    const botRowStyle = "chat-message-bot text-start";

    return (
        <Container className={"p-0 d-flex flex-column overflow-hidden h-90vh "} >
            <NavHeader title="Chat">
                <InfoModalButton
                    titleText={"Chat History"}
                    modalText={"Chat history is only available for the last 7 days"} />
                </NavHeader>
            <div
                className="mx-0 px-0 d-flex flex-column flex-fill overflow-hidden" >
                <div
                    id="chatHistory"
                    className="border rounded m-0 p-1 overflow-y-auto flex-1"
                    ref={chatHistoryRef}
                >
                    {messages.map((message, i) => {
                        const isUser = message.type === MessageType.USER;
                        return (
                            <Container key={i} className={"p-0"}>
                                <div className={`d-flex ${isUser ? 'justify-content-end' : 'justify-content-start'} mt-2`}>
                                    <div className={`p-2 rounded chat-bubble ${isUser ? userRowStyle : botRowStyle}`}>
                                        {message.content}
                                    </div>
                                </div>
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
                    onKeyUp={handleKeyUp}
                />
            </div>
        </Container>
    );
}

export default Chat;
