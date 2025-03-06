package com.seebie.server.controller;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @GetMapping("/user/{publicId}/chat")
    public List<MessageDto> getChatHistory(@PathVariable UUID publicId) {

        return messageService.getMessages(publicId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @PostMapping("/user/{publicId}/chat")
    public MessageDto submitPrompt(@Valid @RequestBody MessageDto prompt, @PathVariable UUID publicId) {

        return messageService.processPrompt(prompt, publicId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @DeleteMapping("/user/{publicId}/chat")
    public void deleteChatHistory(@PathVariable UUID publicId) {

        messageService.deleteMessages(publicId);
    }
}
