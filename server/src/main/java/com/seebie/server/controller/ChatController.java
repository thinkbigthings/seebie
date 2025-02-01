package com.seebie.server.controller;

import com.seebie.server.dto.PromptResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping("/api")
public class ChatController {



    @PreAuthorize("hasRole('ROLE_ADMIN') || #publicId == authentication.principal.publicId")
    @RequestMapping(value="/user/{publicId}/chat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PromptResponse submitPrompt(@Valid @RequestBody String prompt, @PathVariable String publicId) {

        return new PromptResponse(prompt.trim(), "LLM Response goes here");
    }

}
