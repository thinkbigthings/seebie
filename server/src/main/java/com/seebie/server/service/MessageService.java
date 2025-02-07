package com.seebie.server.service;

import com.seebie.server.AppProperties;
import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageType;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final OpenAiChatModel chatModel;
    private final MessagePersistenceService messagePersistenceService;
    private final SystemMessage systemMessage;

    public MessageService(OpenAiChatModel chatModel,
                          MessagePersistenceService messagePersistenceService,
                          AppProperties appProperties)
    {
        this.chatModel = chatModel;
        this.messagePersistenceService = messagePersistenceService;
        this. systemMessage = new SystemMessage(appProperties.ai().system().prompt());
    }

    public List<MessageDto> getMessages(UUID publicId) {
        var sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        return messagePersistenceService.getChatHistory(publicId, sevenDaysAgo);
    }

    public MessageDto processPrompt(MessageDto userPrompt, UUID publicId) {

        var sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        var chatHistory = messagePersistenceService.getChatHistory(publicId, sevenDaysAgo);

        var messagesToSend = new ArrayList<Message>();

        messagesToSend.add(systemMessage);
        chatHistory.stream().map(MessageService::dtoToSpringAi).forEach(messagesToSend::add);
        messagesToSend.add(new UserMessage(userPrompt.content()));

        var options = OpenAiChatOptions.builder().user(publicId.toString()).build();
        var chatResponse = chatModel.call(new Prompt(messagesToSend, options));

        // there could be multiple completions,
        // but spring.ai.openai.chat.options.n is configured to 1
        // so it's ok to use .findFirst()
        var response = chatResponse.getResults().stream()
                .findFirst()
                .map(gen -> gen.getOutput().getText())
                .map(text -> new MessageDto(text, MessageType.ASSISTANT))
                .orElseThrow(() -> new RuntimeException("Nothing was generated"));

        messagePersistenceService.saveExchange(publicId, userPrompt, response);

        return response;
    }

    /**
     *  Although OpenAiChatModel takes a List of Message interfaces,
     *  it actually depends on Spring's concrete classes internally,
     *  so we can't just implement the interfaces ourselves.
     */
    public static Message dtoToSpringAi(MessageDto messageDto) {
        return switch(messageDto.type()) {
            case ASSISTANT -> new AssistantMessage(messageDto.content());
            case USER -> new UserMessage(messageDto.content());
        };
    }

    public static ChatResponse toChatResponse(String content) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(content))));
    }
}
