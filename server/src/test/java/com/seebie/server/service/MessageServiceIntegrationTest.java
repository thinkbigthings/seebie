package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageType;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.UUID;

import static com.seebie.server.service.MessageService.CANNED_RESPONSE;
import static com.seebie.server.service.MessageService.toChatResponse;
import static com.seebie.server.test.data.TestData.randomUserMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

// This tells Spring to provide a mock instead of a real bean
class MessageServiceIntegrationTest extends IntegrationTest {

     @MockitoBean
     private OpenAiChatModel chatModel;

     @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.when(chatModel.call(any(Prompt.class))).thenReturn(toChatResponse(CANNED_RESPONSE));
    }

    @Test
    public void testConversation() {

        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        UUID publicId = UUID.fromString(userService.getUserByEmail(registration.email()).publicId());

        var knownConversation = new ArrayList<MessageDto>();

        var userMessage = randomUserMessage();
        var response = messageService.processPrompt(userMessage, publicId);

        knownConversation.add(userMessage);
        knownConversation.add(response);

        assertEquals(CANNED_RESPONSE, response.content());
        assertEquals(MessageType.ASSISTANT, response.type());

        userMessage = randomUserMessage();
        response = messageService.processPrompt(userMessage, publicId);

        knownConversation.add(userMessage);
        knownConversation.add(response);

        var storedConversation = messageService.getMessages(publicId);

        assertEquals(knownConversation, storedConversation);
    }


}
