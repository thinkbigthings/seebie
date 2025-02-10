package com.seebie.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger LOG = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity<>(createBody(BAD_REQUEST, ex), new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> handleAccessDeniedException(AccessDeniedException ex) {
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity<>(createBody(FORBIDDEN, ex), new HttpHeaders(), FORBIDDEN);
    }

    public Map<String, String> createBody(HttpStatus status, Exception ex) {

        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());
        body.put("status", String.valueOf(status.value()));
        body.put("reason", status.getReasonPhrase());
        body.put("exception", ex.toString());

        return body;
    }
}
