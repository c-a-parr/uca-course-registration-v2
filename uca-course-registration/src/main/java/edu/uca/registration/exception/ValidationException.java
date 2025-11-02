package edu.uca.registration.exception;

// Throws when a user inputs something that does not match what is expected
public class ValidationException extends RuntimeException { public ValidationException(String m){super(m);} }
