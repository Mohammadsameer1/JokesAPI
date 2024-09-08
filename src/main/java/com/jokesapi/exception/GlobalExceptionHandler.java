
package com.jokesapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ServerWebInputException.class)
	public ResponseEntity<ErrorResponse> handleInvalidInput(ServerWebInputException e) {
		log.warn("Invalid input error: {}", e.getReason());
		ErrorResponse errorResponse = new ErrorResponse("Invalid Input", e.getReason());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("An unexpected error occurred: {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse("Internal Server Error",
				"An unexpected error occurred: " + e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(JokeFetchException.class)
	public ResponseEntity<ErrorResponse> handleJokeFetchException(JokeFetchException e) {
		log.error("Joke fetch error: {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse("Joke Fetch Error", e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidJokeResponseException.class)
	public ResponseEntity<ErrorResponse> handleInvalidJokeResponseException(InvalidJokeResponseException e) {
		log.error("Invalid joke response error: {}", e.getMessage());
		ErrorResponse errorResponse = new ErrorResponse("Invalid Joke Response", e.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException ex) {
		log.error("Data access error: {}", ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse("Data Access Error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
