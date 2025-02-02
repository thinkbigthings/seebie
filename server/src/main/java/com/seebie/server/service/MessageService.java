package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.dto.PromptResponse;
import com.seebie.server.entity.Message;
import com.seebie.server.entity.MessageType;
import com.seebie.server.repository.MessageRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

    private final OpenAiChatModel chatModel;
    private final UserRepository userRepo;
    private final MessageRepository messageRepo;

    public MessageService(UserRepository repo, OpenAiChatModel chatModel, MessageRepository messageRepo) {
        this.userRepo = repo;
        this.chatModel = chatModel;
        this.messageRepo = messageRepo;
    }

    @Transactional
    public List<MessageDto> getMessages(String publicId) {
         return messageRepo.findAllByUserPublicId(publicId);
    }

    @Transactional
    public PromptResponse processPrompt(String prompt, String publicId) {

        var trimmedPrompt = prompt.trim();
        var response = "LLM response goes here"; // chatModel.call(trimmedPrompt);

        var user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("No user found: " + publicId) );

        messageRepo.save(new Message(user, trimmedPrompt, MessageType.USER));
        messageRepo.save(new Message(user, response, MessageType.ASSISTANT));

        return new PromptResponse(trimmedPrompt, response);
    }

}
