package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageType;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.seebie.server.service.MessageService.toChatResponse;
import static com.seebie.server.test.data.TestData.randomUserMessage;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class MessageServiceTest {

    private final OpenAiChatModel chatModel = Mockito.mock(OpenAiChatModel.class);
    private final MessagePersistenceService messagePersistenceService = Mockito.mock(MessagePersistenceService.class);

    private MessageService service;

    private final MessageDto userPrompt = randomUserMessage();

    @BeforeEach
    public void setup() {

        service = new MessageService(chatModel, messagePersistenceService, TestData.newAppProperties(30));

        when(messagePersistenceService.getChatHistory(any(UUID.class), any(Instant.class))).thenReturn(List.of());
    }

    @Test
    public void testGetMessages() {
        var messages= service.getMessages(randomUUID());
        assertNotNull(messages);
    }

    @Test
    public void testProcessPrompt() {

        when(chatModel.call(any(Prompt.class))).thenReturn(toChatResponse("Live Response"));

        var response= service.processPrompt(userPrompt, randomUUID());

        assertEquals("Live Response", response.content());
    }

    @Test
    public void testEmptyResponse() {

        when(chatModel.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of()));

        assertThrows(RuntimeException.class, () -> service.processPrompt(userPrompt, randomUUID()));
    }
}
