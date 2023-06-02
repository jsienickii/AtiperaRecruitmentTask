package pl.sienicki.atipera.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.sienicki.atipera.exception.CustomExceptionResponse;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Object> handleMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        String message = "The requested header: Accept:'application/xml' is not acceptable.";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CustomExceptionResponse errorResponse = new CustomExceptionResponse(HttpStatus.NOT_ACCEPTABLE.value(), message);
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.NOT_ACCEPTABLE);
    }

}