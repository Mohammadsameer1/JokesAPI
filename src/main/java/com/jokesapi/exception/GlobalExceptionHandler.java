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
	public ResponseEntity<String> handleInvalidInput(ServerWebInputException e) {

		log.warn("Invalid input error: {}", e.getReason());
		return new ResponseEntity<>("invalid input " + e.getReason(), HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e) {
		log.error("An unexpected error occurred: {}", e.getMessage());

		return new ResponseEntity<>("an error Occurred" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
