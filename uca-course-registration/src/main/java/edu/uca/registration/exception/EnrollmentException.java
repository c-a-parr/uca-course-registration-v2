package edu.uca.registration.exception;

// Throws for enrollment-specific illegal operations (e.g. duplicates, wrong state, & others)
public class EnrollmentException extends RuntimeException { public EnrollmentException(String m){super(m);} }