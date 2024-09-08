package com.jokesapi.exception;

@SuppressWarnings("serial")
public class InvalidJokeResponseException extends RuntimeException {
	

	public InvalidJokeResponseException(String message) {
		super(message);
	}
}