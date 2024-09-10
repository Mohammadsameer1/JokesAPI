package com.jokesapi.exception;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebInputException;

 class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
     void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
     void testHandleException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError()); 
        assertEquals("An unexpected error occurred: Unexpected error", response.getBody().getMessage());  
    }

    @Test
    void testHandleInvalidInput() {
        ServerWebInputException exception = mock(ServerWebInputException.class);
        when(exception.getReason()).thenReturn("Invalid input");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidInput(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Input", response.getBody().getError()); 
        assertEquals("Invalid input", response.getBody().getMessage());  
    }

    @Test
     void testHandleJokeFetchException() {
        JokeFetchException exception = new JokeFetchException("Joke fetch failed");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleJokeFetchException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Joke Fetch Error", response.getBody().getError());  
        assertEquals("Joke fetch failed", response.getBody().getMessage()); 
    }

    @Test
     void testHandleInvalidJokeResponseException() {
        InvalidJokeResponseException exception = new InvalidJokeResponseException("Invalid joke response");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidJokeResponseException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Joke Response", response.getBody().getError());
        assertEquals("Invalid joke response", response.getBody().getMessage()); 
    }

    @Test
     void testHandleDataAccessException() {
        DataAccessException exception = new DataAccessException("Data access error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataAccessException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Data Access Error", response.getBody().getError());  
        assertEquals("Data access error", response.getBody().getMessage());  
    }
}
