package com.jokesapi.exception;


@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

 public DataAccessException(String message) {
     super(message);
 }


 public DataAccessException(String message, Throwable cause) {
     super(message, cause);
 }
}