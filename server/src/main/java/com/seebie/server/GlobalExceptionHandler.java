package com.seebie.server;

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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(createBody(BAD_REQUEST, ex), new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(createBody(FORBIDDEN, ex), new HttpHeaders(), FORBIDDEN);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Object> exception(Exception ex) {
//        return new ResponseEntity<>(getBody(INTERNAL_SERVER_ERROR, ex, "Something Went Wrong"), new HttpHeaders(), INTERNAL_SERVER_ERROR);
//    }

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
