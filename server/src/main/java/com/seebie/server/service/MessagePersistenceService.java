package com.seebie.server.service;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.entity.MessageEntity;
import com.seebie.server.entity.MessageType;
import com.seebie.server.repository.MessageRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class MessagePersistenceService {

    private final UserRepository userRepo;
    private final MessageRepository messageRepo;

    public MessagePersistenceService(UserRepository repo, MessageRepository messageRepo) {
        this.userRepo = repo;
        this.messageRepo = messageRepo;
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getChatHistory(UUID publicId, Instant earliest) {
        return messageRepo.findSince(publicId, earliest);
    }

    @Transactional
    public void saveExchange(UUID userPublicId, MessageDto userPrompt, MessageDto chatResponse) {

        var user = userRepo.findByPublicId(userPublicId)
                .orElseThrow(() -> new EntityNotFoundException("No user found: " + userPublicId) );

        // make sure the timestamp of the user message is before the timestamp of the response
        // so they appear in the right order
        messageRepo.save(new MessageEntity(user, userPrompt.content(), MessageType.USER));
        messageRepo.save(new MessageEntity(user, chatResponse.content(), MessageType.ASSISTANT));
    }

}
