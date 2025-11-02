package edu.uca.registration;

import edu.uca.registration.repo.csv.CsvCourseRepository;
import edu.uca.registration.repo.csv.CsvEnrollmentRepository;
import edu.uca.registration.repo.csv.CsvStudentRepository;
import edu.uca.registration.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SystemWorkflowTest {
    @TempDir Path tmp;

    @Test
    void addEnrollDropPromoteList_endToEnd() {
        var svc = new RegistrationService(
                new CsvStudentRepository(tmp.resolve("students.csv")),
                new CsvCourseRepository(tmp.resolve("courses.csv")),
                new CsvEnrollmentRepository(tmp.resolve("enrollments.csv"))
        );

        svc.addStudent("B001","A","a@a.com");
        svc.addStudent("B002","B","b@a.com");
        svc.addStudent("B003","C","c@a.com");
        svc.addCourse("CS","Course",1);

        assertEquals("ENROLLED", svc.enroll("B001","CS"));
        assertEquals("WAITLIST", svc.enroll("B002","CS"));
        assertEquals("WAITLIST", svc.enroll("B003","CS"));

        // Drop enrolled -> promote first waitlisted (B002)
        var msg = svc.drop("B001","CS");
        assertTrue(msg.contains("PROMOTED B002"));

        // sanity
        assertEquals(3, svc.getStudents().size());
        assertEquals(1, svc.getCourses().size());
    }
}

