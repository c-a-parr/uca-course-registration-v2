package edu.uca.registration.model;

/* Immutable Student value object
*   creates constructor, accessors, equals, hashCode, toString.*/
public record Student(String bannerId, String name, String email) {}