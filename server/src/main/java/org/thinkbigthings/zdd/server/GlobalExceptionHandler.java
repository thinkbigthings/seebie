package org.thinkbigthings.zdd.server;

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
        return new ResponseEntity<>(createBody(BAD_REQUEST, ex), new HttpHeaders(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(createBody(FORBIDDEN, ex), new HttpHeaders(), FORBIDDEN);
    }

    // there's no standard for what this return code should be, other options include 412 or plain 400
    @ExceptionHandler(IncompatibleClientVersionException.class)
    public ResponseEntity<Map<String,String>> handleIncompatibleClient(IncompatibleClientVersionException ex) {
        return new ResponseEntity<>(createBody(NOT_ACCEPTABLE, ex), new HttpHeaders(), NOT_ACCEPTABLE);
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
        body.put("error", status.getReasonPhrase());
        body.put("exception", ex.toString());

        Throwable cause = ex.getCause();
        if (cause != null) {
            body.put("exceptionCause", ex.getCause().toString());
        }
        return body;
    }
}
