package edu.uca.registration.util;

import edu.uca.registration.exception.ValidationException;

 // Centralized validation rules using Regex
 // Throw ValidationException errors for users
public final class Validation {
    public static void nonEmpty(String s, String field) {
        if (s == null || s.isBlank()) throw new ValidationException(field + " must not be empty");
    }
    public static void email(String e) {
        if (e == null || !e.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new ValidationException("Invalid email");
    }
    public static void bannerId(String id) {
        if (id == null || !id.matches("^B\\d{3,}$"))
            throw new ValidationException("Invalid Banner ID");
    }
    public static void courseCode(String code) { nonEmpty(code, "course code"); }
    public static void capacity(int c) {
        if (c < 1 || c > 500) throw new ValidationException("Capacity 1–500");
    }
}

