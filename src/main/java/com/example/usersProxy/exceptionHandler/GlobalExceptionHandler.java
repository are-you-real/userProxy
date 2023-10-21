package com.example.usersProxy.exceptionHandler;

import com.example.usersProxy.exception.UserInternalServerException;
import com.example.usersProxy.exception.UserNotFoundException;
import com.example.usersProxy.response.userProxy.UserProxyApiErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class})
    public final ResponseEntity<Object> handleUserNotFoundException(Exception ex) throws JsonProcessingException {
        UserProxyApiErrorResponse response = new UserProxyApiErrorResponse("User does not exist");
        System.out.println("User service not found error " + ex);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserInternalServerException.class})
    public final ResponseEntity<Object> handleUserInternalServerException(Exception ex) throws JsonProcessingException {
        UserProxyApiErrorResponse response = new UserProxyApiErrorResponse("Internal server error");
        System.out.println("User service internal error " + ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({WebClientResponseException.class})
    public final ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
        UserProxyApiErrorResponse response = new UserProxyApiErrorResponse("Internal server error");
        System.out.println("WebClient error " + ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<Object> handleUncaughtExceptions(Exception ex) {
        UserProxyApiErrorResponse response = new UserProxyApiErrorResponse("Internal server error");
        System.out.println("Unhandled exception during REST endpoint call" + ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
