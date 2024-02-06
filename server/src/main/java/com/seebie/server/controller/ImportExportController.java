package com.seebie.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.UploadResponse;
import com.seebie.server.dto.UserData;
import com.seebie.server.service.ImportExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

// if we use server.servlet.context-path=/api, static content and API all come from the same base
// so we can use that for api-only requests only if the UI is served separately
@RestController
@RequestMapping("/api")
public class ImportExportController {

    private static Logger LOG = LoggerFactory.getLogger(ImportExportController.class);

    private final ImportExportService importExportService;
    private ObjectMapper jsonMapper;

    // if there's only one constructor, can omit Autowired and Inject
    public ImportExportController(MappingJackson2HttpMessageConverter converter, ImportExportService importExportService) {
        this.jsonMapper = converter.getObjectMapper();
        this.importExportService = importExportService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/export/json", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> exportUserData(@PathVariable String username) {

        String filename = "seebie-data-" + username + ".json";
        String headerValue = "attachment; filename=" + filename;

        String json = toJson(importExportService.retrieveUserData(username));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(json.getBytes(UTF_8));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/import/json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public UploadResponse importUserData(@PathVariable String username, @RequestParam("file") MultipartFile file) throws IOException {

        String rawJson = new String(file.getBytes(), UTF_8);

        LOG.info(STR."Import started for user \{username}");

        var userData = parseJson(rawJson);
        if(userData.sleepData().isEmpty()) {
            throw new IllegalArgumentException("No sleep data found in the input json");
        }

        long numImported = importExportService.saveUserData(username, userData);

        LOG.info(STR."Imported \{numImported} records for \{username}");

        return new UploadResponse(numImported, username);
    }

    public String toJson(UserData userData) {
        try {
            return jsonMapper.writeValueAsString(userData);
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public UserData parseJson(String userDataJson) {
        try {
            return jsonMapper.readValue(userDataJson, UserData.class);
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}