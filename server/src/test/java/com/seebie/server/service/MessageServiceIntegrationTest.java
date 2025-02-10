package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageType;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static com.seebie.server.service.MessageServiceTest.toChatResponse;
import static com.seebie.server.test.data.TestData.randomUserMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class MessageServiceIntegrationTest extends IntegrationTest {

    public final static String CANNED_RESPONSE = "LLM response";

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

        // Arrange: Set up test data per test
        UUID publicId = saveNewUser();

        // Act: Send first message and capture response
        var firstUserMessage = randomUserMessage();
        var firstResponse = messageService.processPrompt(firstUserMessage, publicId);

        // Assert: Validate the first response details
        assertEquals(CANNED_RESPONSE, firstResponse.content());
        assertEquals(MessageType.ASSISTANT, firstResponse.type());

        // Act: Send a second message and capture the subsequent response
        MessageDto secondUserMessage = randomUserMessage();
        MessageDto secondResponse = messageService.processPrompt(secondUserMessage, publicId);

        // Assert: Validate the overall conversation history
        List<MessageDto> expectedConversation = List.of(
                firstUserMessage, firstResponse,
                secondUserMessage, secondResponse
        );
        List<MessageDto> actualConversation = messageService.getMessages(publicId);
        assertEquals(expectedConversation, actualConversation);

    }

    private UUID saveNewUser() {
        var registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        return UUID.fromString(userService.getUserByEmail(registration.email()).publicId());
    }
}
