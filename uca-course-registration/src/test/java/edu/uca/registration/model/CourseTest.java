package edu.uca.registration.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    @Test
    void rosterWaitlistMutationsWorkAndAreFifo() {
        var c = new Course("CS", "Title", 2, null, null);

        c.addToRoster("B001");
        c.addToRoster("B002");
        assertEquals(2, c.rosterView().size());

        c.addToWaitlist("B003");
        c.addToWaitlist("B004");
        assertEquals(2, c.waitlistView().size());

        // FIFO promotion behavior helpers:
        assertEquals("B003", c.popWaitlisted());
        assertEquals("B004", c.popWaitlisted());
        assertNull(c.popWaitlisted());

        assertTrue(c.removeFromRoster("B002"));
        assertFalse(c.removeFromRoster("B999"));
    }
}

