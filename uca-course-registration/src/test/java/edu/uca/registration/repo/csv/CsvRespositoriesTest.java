package edu.uca.registration.repo.csv;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.EnrollmentRepository;
import edu.uca.registration.repo.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvRepositoriesTest {

    @TempDir Path tmp;

    private Path p(String name) { return tmp.resolve(name); }

    @Test
    void studentCsv_roundtrip_createOnFirstSave() throws Exception {
        StudentRepository sr = new CsvStudentRepository(p("students.csv"));

        // create on first save
        sr.save(new Student("B001", "Alice", "a@b.com"));
        assertTrue(Files.exists(p("students.csv")));

        assertEquals(1, sr.findAll().size());
        assertTrue(sr.findById("B001").isPresent());

        // upsert
        sr.save(new Student("B001", "Alice2", "a2@b.com"));
        assertEquals("Alice2", sr.findById("B001").get().name());
    }

    @Test
    void courseAndEnrollmentCsv_reconstructsRosterAndWaitlist() {
        CourseRepository cr = new CsvCourseRepository(p("courses.csv"));
        EnrollmentRepository er = new CsvEnrollmentRepository(p("enrollments.csv"));

        // write course metadata
        cr.save(new Course("CS1", "Course", 1, null, null));

        // add enrollment rows directly
        er.saveEnrollment("CS1", "B001"); // roster full (cap=1)
        er.saveWaitlist("CS1", "B002");
        er.saveWaitlist("CS1", "B003");

        // reconstruct into in-memory course objects
        er.loadInto(cr);

        var c = cr.findByCode("CS1").orElseThrow();
        assertEquals(1, c.rosterView().size());
        assertEquals(2, c.waitlistView().size());
        assertEquals("B002", c.waitlistView().peekFirst()); // FIFO preserved
    }
}

