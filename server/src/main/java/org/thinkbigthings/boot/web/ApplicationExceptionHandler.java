package org.thinkbigthings.boot.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {

    // TODO log the exception/stacktrace and return some kind of error object
    // don't return the stack trace
    
    // TODO add email service, send notification if an unknown exception is caught
    
   @ExceptionHandler
   @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
   public @ResponseBody String handleException(HttpRequestMethodNotSupportedException ex) {
      return getStackTrace(ex);
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.UNAUTHORIZED)
   public @ResponseBody String handleException(AccessDeniedException ex) {
      return ex.getMessage() + getStackTrace(ex);
   }
   
   @ExceptionHandler
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public @ResponseBody String handleException(InvalidRequestBodyException ex) {
      return ex.getMessage() + getStackTrace(ex);
   }
   
   @ExceptionHandler
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public @ResponseBody String handleNotFound(EntityNotFoundException ex) {
      return getStackTrace(ex);
   }
   
   @ExceptionHandler
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public @ResponseBody String genericHandleException(Exception ex) {
      return getStackTrace(ex);
   }

   public static String getStackTrace(Throwable throwable)
   {
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      throwable.printStackTrace(printWriter);
      return result.toString();
   }
}
