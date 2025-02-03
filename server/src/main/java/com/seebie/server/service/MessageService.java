package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageEntity;
import com.seebie.server.entity.MessageType;
import com.seebie.server.repository.MessageRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;


@Service
public class MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

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
    public MessageDto processPrompt(String userPrompt, UUID publicId) {

        var user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("No user found: " + publicId) );

        var sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        Prompt conversation = new Prompt(messageRepo.findSince(publicId, sevenDaysAgo));
        var chatResponse = useRealLLM
                ? chatModel.call(conversation)
                : new ChatResponse(List.of(new Generation(new AssistantMessage("LLM response")))) ;

        // there could be multiple completions, but n is configured to 1
        var response = chatResponse.getResults().stream()
                .findFirst()
                .map(gen -> gen.getOutput().getText())
                .map(text -> new MessageEntity(user, text, MessageType.ASSISTANT))
                .orElseThrow(() -> new RuntimeException("Nothing was generated"));

        messageRepo.save(new MessageEntity(user, userPrompt, MessageType.USER));
        messageRepo.save(response);

        return new MessageDto(response.getText(), MessageType.ASSISTANT);
    }

}
