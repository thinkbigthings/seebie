package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageEntity;
import com.seebie.server.entity.MessageType;
import com.seebie.server.repository.MessageRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class MessageService {

    private final OpenAiChatModel chatModel;
    private final UserRepository userRepo;
    private final MessageRepository messageRepo;
    private final boolean useRealLLM = false;

    public MessageService(UserRepository repo, OpenAiChatModel chatModel, MessageRepository messageRepo) {
        this.userRepo = repo;
        this.chatModel = chatModel;
        this.messageRepo = messageRepo;
    }

    @Transactional
    public List<MessageDto> getMessages(String publicId) {
         return messageRepo.findAllByUserPublicId(UUID.fromString(publicId));
    }

    @Transactional
    public MessageDto processPrompt(MessageDto userPrompt, UUID publicId) {

        var user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("No user found: " + publicId) );

        // make sure the timestamp of the user message is before the timestamp of the response
        // so they appear in the right order
        var userMessage = new MessageEntity(user, userPrompt.content(), MessageType.USER);

        var sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        var chatHistory = messageRepo.findSince(publicId, sevenDaysAgo).stream()
                .map(this::dtoToSpringAi)
                .toList();

        var messagesToSend = new ArrayList<>(chatHistory);
        messagesToSend.add(new UserMessage(userPrompt.content()));

        var chatResponse = useRealLLM
                ? chatModel.call(new Prompt(messagesToSend))
                : new ChatResponse(List.of(new Generation(new AssistantMessage("LLM response")))) ;

        // there could be multiple completions,
        // but spring.ai.openai.chat.options.n is configured to 1
        // so it's ok to use .findFirst()
        var response = chatResponse.getResults().stream()
                .findFirst()
                .map(gen -> gen.getOutput().getText())
                .map(text -> new MessageEntity(user, text, MessageType.ASSISTANT))
                .orElseThrow(() -> new RuntimeException("Nothing was generated"));

        messageRepo.save(userMessage);
        messageRepo.save(response);

        return new MessageDto(response.getText(), MessageType.ASSISTANT);
    }

    /**
     *  Although OpenAiChatModel takes a List of Message interfaces,
     *  it actually depends on Spring's concrete classes internally,
     *  so we can't just implement the interfaces ourselves.
     */
    public Message dtoToSpringAi(MessageDto messageDto) {
        return switch(messageDto.type()) {
            case ASSISTANT -> new AssistantMessage(messageDto.content());
            case USER -> new UserMessage(messageDto.content());
        };
    }

}
