package pl.sienicki.atipera.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import pl.sienicki.atipera.exception.CustomExceptionResponse;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionHandlerTest {

    @Test
    void handleMediaTypeNotAcceptableExceptionAndReturn406WithCorrectMessage()  {
        //given
        CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
        String expectedErrorMessage = "The requested header: Accept:'application/xml' is not acceptable.";
        HttpMediaTypeNotAcceptableException exception = new HttpMediaTypeNotAcceptableException(expectedErrorMessage);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept", "application/xml");

        // when
        ResponseEntity<Object> response = exceptionHandler.handleMediaTypeNotAcceptableException(exception);
        CustomExceptionResponse errorResponse = (CustomExceptionResponse) response.getBody();

        // then
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals(expectedErrorMessage, errorResponse.message());
    }
}