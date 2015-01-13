package org.thinkbigthings.boot.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    
    // TODO 0 return some kind of error object
    // want to write application logs to own log file instead of spring.log
    // see section 25.2 http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html
    // http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html
    // http://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html
    
    // TODO 1 add email service, send email notification to admin if an unknown exception is caught
    
    // TODO 1 use email service for password reset feature
    
   @ExceptionHandler
   @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
   public @ResponseBody String handleException(HttpRequestMethodNotSupportedException ex) {
       log.info("Application handled", ex);
      return ex.getMessage();
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.UNAUTHORIZED)
   public @ResponseBody String handleException(AccessDeniedException ex) {
       log.info("Application handled", ex);
      return ex.getMessage();
   }
   
   @ExceptionHandler
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public @ResponseBody String handleException(InvalidRequestBodyException ex) {
       log.info("Application handled", ex);
       return ex.getMessage();
   }

   @ExceptionHandler
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public @ResponseBody String handleNotFound(EntityNotFoundException ex) {
       log.info("Application handled", ex);
       return ex.getMessage();
   }
   
   @ExceptionHandler
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public @ResponseBody String genericHandleException(Exception ex) {
      log.info("Application handled", ex);
      return ex.getMessage();
   }

   public static String getStackTrace(Throwable throwable)
   {
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      throwable.printStackTrace(printWriter);
      return result.toString();
   }
}
