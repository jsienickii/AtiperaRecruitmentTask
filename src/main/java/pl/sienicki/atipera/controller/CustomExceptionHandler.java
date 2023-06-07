package pl.sienicki.atipera.controller;

import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.sienicki.atipera.dto.CustomExceptionResponse;

@ControllerAdvice
public class CustomExceptionHandler {


    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Object> handleFeignExceptionNotFound(FeignException.NotFound ex) {
        String message = "User not found";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CustomExceptionResponse errorResponse = new CustomExceptionResponse(HttpStatus.NOT_FOUND.value(), message);
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.NOT_FOUND);
    }

}