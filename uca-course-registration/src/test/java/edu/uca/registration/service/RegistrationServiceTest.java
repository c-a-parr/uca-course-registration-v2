package edu.uca.registration.service;

import edu.uca.registration.exception.EnrollmentException;
import edu.uca.registration.exception.NotFoundException;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.EnrollmentRepository;
import edu.uca.registration.repo.StudentRepository;
import edu.uca.registration.repo.csv.CsvCourseRepository;
import edu.uca.registration.repo.csv.CsvEnrollmentRepository;
import edu.uca.registration.repo.csv.CsvStudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @TempDir Path tmp;

    StudentRepository students;
    CourseRepository courses;
    EnrollmentRepository enrollments;
    RegistrationService svc;

    @BeforeEach
    void setup() {
        students = new CsvStudentRepository(tmp.resolve("students.csv"));
        courses  = new CsvCourseRepository(tmp.resolve("courses.csv"));
        enrollments = new CsvEnrollmentRepository(tmp.resolve("enrollments.csv"));

        enrollments.loadInto(courses);
        svc = new RegistrationService(students, courses, enrollments);

        // seed a little data
        svc.addStudent("B001", "Alice", "a@b.com");
        svc.addStudent("B002", "Bob",   "b@b.com");
        svc.addStudent("B003", "Cara",  "c@b.com");
        svc.addCourse("CS1", "Course 1", 2);
    }

    @Test
    void enroll_whenSpace_enrolled() {
        var msg = svc.enroll("B001", "CS1");
        assertEquals("ENROLLED", msg);
    }

    @Test
    void enroll_whenFull_waitlisted() {
        svc.enroll("B001", "CS1");
        svc.enroll("B002", "CS1");
        var msg = svc.enroll("B003", "CS1");
        assertEquals("WAITLIST", msg);
    }

    @Test
    void duplicateEnroll_disallowed() {
        svc.enroll("B001", "CS1");
        assertThrows(EnrollmentException.class, () -> svc.enroll("B001", "CS1"));
    }

    @Test
    void drop_promotesFirstWaitlisted_fifo() {
        // fill + waitlist
        svc.enroll("B001", "CS1");
        svc.enroll("B002", "CS1");
        svc.enroll("B003", "CS1"); // waitlist 1st

        // drop one → B003 promoted
        var msg = svc.drop("B001", "CS1");
        assertTrue(msg.contains("PROMOTED B003"));
    }

    @Test
    void unknownStudentOrCourse_throwsNotFound() {
        assertThrows(NotFoundException.class, () -> svc.enroll("NOPE", "CS1"));
        assertThrows(NotFoundException.class, () -> svc.enroll("B001", "NOPE"));
    }
}

