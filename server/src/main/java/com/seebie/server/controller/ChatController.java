package com.seebie.server.controller;

import com.seebie.server.dto.MessageDto;
import com.seebie.server.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping("/api")
public class ChatController {

    private static Logger LOG = LoggerFactory.getLogger(ChatController.class);

    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value="/user/{publicId}/chat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<MessageDto> getChatHistory(@PathVariable String publicId) {

        return messageService.getMessages(publicId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value="/user/{publicId}/chat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MessageDto submitPrompt(@RequestBody MessageDto prompt, @PathVariable String publicId) {

        return messageService.processPrompt(prompt.content().trim(), UUID.fromString(publicId));
    }

}
