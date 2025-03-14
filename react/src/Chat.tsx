import React, {useCallback, useEffect, useRef, useState} from 'react';
import Container from "react-bootstrap/Container";
import {NavHeader} from "./App";
import {useParams} from "react-router-dom";
import {GET} from "./utility/BasicHeaders.ts";
import {httpDelete, httpPost, PostFetchVariables} from "./utility/apiClient.ts";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {MessageDto, MessageType} from "./types/message.types.ts";
import {faCircle} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InfoModalButton from "./component/InfoModalButton.tsx";
import WarningButton from "./component/WarningButton.tsx";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";



function Chat() {

    const {publicId} = useParams();
    if (publicId === undefined) {
        throw new Error("Public ID is required in the url");
    }

    const chatUrl = `/api/user/${publicId}/chat`

    const [showProcessingIcon, setShowProcessingIcon] = useState<boolean>(false);
    const [prompt, setPrompt] = useState<string>("");

    const queryClient = useQueryClient();

    const fetchChatHistory = () => fetch(chatUrl, GET)
        .then((response) => response.json() as Promise<MessageDto[]>);

    const chatHistoryQuery = useQuery<MessageDto[]>({
        queryFn: fetchChatHistory,
        queryKey: [chatUrl],
        placeholderData: [] as MessageDto[],
        staleTime: Infinity,
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
        if (!prompt.trim()) return;
        setShowProcessingIcon(true);

        const userPrompt: MessageDto = { content: prompt.trim(), type: MessageType.USER };

        queryClient.setQueryData([chatUrl], (oldData: MessageDto[] | undefined) => [
            ...(oldData ?? []),
            userPrompt,
        ]);

        uploadMessageMutation.mutate({
            url: chatUrl,
            body: userPrompt
        });
        setPrompt('');
    };

    // Use onKeyDown to capture Enter and prevent newline insertion
    const handleKeyDown = useCallback((e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            submitPrompt();
        }
    }, [prompt]);

    // Auto-scroll to bottom whenever chatHistory updates.
    const bottomRef = React.useRef<HTMLDivElement>(null);
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [chatHistoryQuery.data]);

    const deleteChat = () => {
        httpDelete(chatUrl).then(response => queryClient.invalidateQueries({queryKey: [chatUrl]}));
    }

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
                >
                    {chatHistoryQuery.data?.map((message, i) => {
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
                    <div ref={bottomRef} />
                </div>

                <textarea
                    className="form-control"
                    id="prompt"
                    placeholder="Press Enter to send"
                    rows={3}
                    value={prompt}
                    onChange={(e) => setPrompt(e.target.value)}
                    onKeyDown={handleKeyDown}
                />
            </div>
        </Container>
    );
}

export default Chat;
