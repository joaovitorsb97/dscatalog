package com.joaovitorsb.dscatalog.controllers.exception;

import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ExceptionHandlerController{


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException exception, HttpServletRequest request){
        StandardError standardError = new StandardError(Instant.now(),
                                                        HttpStatus.NOT_FOUND.value(),
                                                        "Resource not found",
                                                        exception.getMessage(),
                                                        request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(standardError);
    }
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> databaseError(DatabaseException exception, HttpServletRequest request){
        StandardError standardError = new StandardError(Instant.now(),
                                                        HttpStatus.BAD_REQUEST.value(),
                                                        "Database error",
                                                        exception.getMessage(),
                                                        request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validationError(MethodArgumentNotValidException exception, HttpServletRequest request){
        ValidationError validationError = new ValidationError(Instant.now(),
                                                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                                                        "Validation Error",
                                                        exception.getMessage(),
                                                        request.getRequestURI());

        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()){
            validationError.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(validationError);
    }
}
