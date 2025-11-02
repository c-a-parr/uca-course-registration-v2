package edu.uca.registration;

import edu.uca.registration.app.Cli;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.EnrollmentRepository;
import edu.uca.registration.repo.StudentRepository;
import edu.uca.registration.repo.csv.CsvCourseRepository;
import edu.uca.registration.repo.csv.CsvEnrollmentRepository;
import edu.uca.registration.repo.csv.CsvStudentRepository;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.util.Config;

 // Application entry point.
 // Wires up CSV-backed repositories, builds the service layer, then hands off to the CLI
 // No business rules or persistence logic live here—just composition.
public class Main {
    public static void main(String[] args) {

        // Repos (CSV-backed)
        StudentRepository students = new CsvStudentRepository(Config.path("csv.students"));
        CourseRepository  courses  = new CsvCourseRepository(Config.path("csv.courses"));
        EnrollmentRepository enroll = new CsvEnrollmentRepository(Config.path("csv.enrollments"));

        // Reconstruct enrollments (roster/waitlist) into in-memory Course objects
        enroll.loadInto(courses);

        // Service layer/rules
        RegistrationService svc = new RegistrationService(students, courses, enroll);

        // Optional seed
        if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
            seedDemoData(svc);
        }

        // CLI
        new Cli(svc).run();
    }

    // Old default records for some pre-loaded info
    private static void seedDemoData(RegistrationService svc) {
        svc.addStudent("B001", "Alice", "alice@uca.edu");
        svc.addStudent("B002", "Brian", "brian@uca.edu");
        svc.addCourse("CSCI4490", "Software Engineering", 2);
        svc.addCourse("MATH1496", "Calculus I", 50);
    }
}

