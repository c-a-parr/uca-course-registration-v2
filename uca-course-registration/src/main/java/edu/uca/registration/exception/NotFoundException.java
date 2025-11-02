package edu.uca.registration.exception;

// Throws for cases where an id or code does not exist
public class NotFoundException    extends RuntimeException { public NotFoundException(String m){super(m);} }