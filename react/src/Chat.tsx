import React, {useCallback, useLayoutEffect, useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import {useParams} from "react-router-dom";
import {basicHeader, GET} from "./utility/BasicHeaders.ts";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {MessageDto, MessageType} from "./types/message.types.ts";
import {faCircle} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InfoModalButton from "./component/InfoModalButton.tsx";
import WarningButton from "./component/WarningButton.tsx";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";

interface PostFetchVariables {
    url: string;
    body: MessageDto;
}

const httpPost = async <T,>(url: string, body: Record<string, unknown>) => {

    const bodyString = JSON.stringify(body);
    const requestHeaders: HeadersInit = basicHeader();

    const requestMeta: RequestInit = {
        headers: requestHeaders,
        method: 'POST',
        body: bodyString
    };

    const response = await fetch(url, requestMeta);
    const data = await response.json();
    return data as T;
}

const httpDelete = (url: string) => {

    const requestHeaders: HeadersInit = basicHeader();

    const requestMeta = {
        headers: requestHeaders,
        method: 'DELETE'
    };

    return fetch(url, requestMeta);
}


function Chat() {

    const {publicId} = useParams();
    if (publicId === undefined) {
        throw new Error("Public ID is required in the url");
    }

    const chatUrl = `/api/user/${publicId}/chat`

    const [showProcessingIcon, setShowProcessingIcon] = useState<boolean>(false);

    const queryClient = useQueryClient();

    const fetchChatHistory = () => fetch(chatUrl, GET)
        .then((response) => response.json() as Promise<MessageDto[]>);

    const chatHistoryQuery = useQuery<MessageDto[]>({
        queryFn: fetchChatHistory,
        queryKey: [chatUrl],
        initialData: []
    });


    const uploadMessageMutation = useMutation({
        mutationFn: (variables: PostFetchVariables) => httpPost<MessageDto>(variables.url, variables.body),
        onSuccess: (message: MessageDto) => {
            setShowProcessingIcon(false);
            queryClient.setQueryData([chatUrl], (oldData: MessageDto[] | undefined) => [
                ...(oldData ?? []),
                message,
            ]);
        },
    });

    const submitPrompt = () => {
        setShowProcessingIcon(true);
        const prompt = retrieveUserPrompt();
        if (!prompt) return;

        const userPrompt: MessageDto = { content: prompt, type: MessageType.USER };

        queryClient.setQueryData([chatUrl], (oldData: MessageDto[] | undefined) => [
            ...(oldData ?? []),
            userPrompt,
        ]);

        uploadMessageMutation.mutate({
            url: chatUrl,
            body: userPrompt
        });
    };

    const promptRef = useRef<HTMLTextAreaElement>(null);

    const retrieveUserPrompt = (): string | null => {
        if (!promptRef.current) return null;
        const prompt = promptRef.current.value.trim();
        promptRef.current.value = "";
        return prompt;
    };


    const deleteChat = () => {
        httpDelete(chatUrl).then(response => queryClient.invalidateQueries({queryKey: [chatUrl]}));
    }


    // call the callback function if the enter key was pressed in the textarea
    const handleKeyUp = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter') {
            submitPrompt();
        }
    }, []);


    // Auto-scroll to bottom whenever chatHistory updates
    const chatHistoryRef = useRef<HTMLDivElement>(null);

    useLayoutEffect(() => {
        const chatDiv = chatHistoryRef.current;
        if (!chatDiv) return;

        const isAtBottom = chatDiv.scrollHeight - chatDiv.clientHeight <= chatDiv.scrollTop + 10;
        if (isAtBottom) {
            chatDiv.scrollTop = chatDiv.scrollHeight;
        }
    }, [chatHistoryQuery.data]);

    const userRowStyle = "chat-message-user text-start";
    const botRowStyle = "chat-message-bot text-start";

    return (
        <Container className={"p-0 d-flex flex-column overflow-hidden h-90vh "} >
            <NavHeader title="Chat">
                <InfoModalButton
                    className={"me-1"}
                    titleText={"Chat History"}
                    modalText={"Chat history is only available for the last 7 days"} />
                <WarningButton buttonText="Delete" onConfirm={deleteChat}>
                    This deletes the entire conversation and cannot be undone. Proceed?
                </WarningButton>
            </NavHeader>
            <div
                className="mx-0 px-0 d-flex flex-column flex-fill overflow-hidden" >
                <div
                    id="chatHistory"
                    className="border rounded m-0 p-1 overflow-y-auto flex-1"
                    ref={chatHistoryRef}
                >
                    {chatHistoryQuery.data.map((message, i) => {
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
                    {showProcessingIcon && (
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
