package org.thinkbigthings.boot.web;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class InvalidRequestBodyException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;
    public InvalidRequestBodyException(BindingResult binding) {
        super("Validation of incoming object failed at " + binding.getNestedPath() + ": " + getFailureMessages(binding));
    }
    
    private static String getFailureMessages(BindingResult binding) {
        String result = "";
        for(ObjectError error : binding.getAllErrors()) {
            result += error.getDefaultMessage();
        }
        return result;
    }
}
