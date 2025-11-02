package edu.uca.registration.util;

import edu.uca.registration.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void nonEmpty_rejectsBlank() {
        var ex = assertThrows(ValidationException.class, () -> Validation.nonEmpty("  ", "name"));
        assertTrue(ex.getMessage().toLowerCase().contains("name"));
    }

    @Test
    void email_acceptsBasicFormat() {
        assertDoesNotThrow(() -> Validation.email("a@b.com"));
    }

    @Test
    void email_rejectsBadFormat() {
        assertThrows(ValidationException.class, () -> Validation.email("not-an-email"));
        assertThrows(ValidationException.class, () -> Validation.email("a@b"));
        assertThrows(ValidationException.class, () -> Validation.email("@b.com"));
    }

    @Test
    void bannerId_rules() {
        assertDoesNotThrow(() -> Validation.bannerId("B001"));
        assertThrows(ValidationException.class, () -> Validation.bannerId("X001"));
        assertThrows(ValidationException.class, () -> Validation.bannerId("B0"));
    }

    @Test
    void capacity_mustBeWithinBounds() {
        assertDoesNotThrow(() -> Validation.capacity(1));
        assertDoesNotThrow(() -> Validation.capacity(500));
        assertThrows(ValidationException.class, () -> Validation.capacity(0));
        assertThrows(ValidationException.class, () -> Validation.capacity(501));
    }
}

