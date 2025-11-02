package edu.uca.registration.service;

import edu.uca.registration.exception.*;
import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.*;
import edu.uca.registration.util.Validation;
import java.util.Collection;

 // Add students/courses with validation
 // Enroll / Drop with capacity enforcement, duplicate checks, FIFO promotion
 // This layer orchestrates repositories but contains no UI/printing.

public final class RegistrationService {
    private final StudentRepository students;
    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;

    public RegistrationService(StudentRepository s, CourseRepository c, EnrollmentRepository e) {
        this.students = s; this.courses = c; this.enrollments = e;
    }


    // Commands
    public void addStudent(String id, String name, String email) {

        Validation.bannerId(id);
        Validation.nonEmpty(name, "name");
        Validation.email(email);

        students.save(new Student(id, name, email));
    }

    public void addCourse(String code, String title, int capacity) {

        Validation.courseCode(code);
        Validation.nonEmpty(title, "title");
        Validation.capacity(capacity);

        courses.save(new Course(code, title, capacity, null, null));
    }

    // Enroll or waitlist a student, returns a status sting
    public String enroll(String bannerId, String courseCode) {
        Student s = students.findById(bannerId)
                .orElseThrow(() -> new NotFoundException("No student " + bannerId));
        Course c = courses.findByCode(courseCode)
                .orElseThrow(() -> new NotFoundException("No course " + courseCode));

        if (c.rosterView().contains(s.bannerId())) throw new EnrollmentException("Already enrolled");
        if (c.waitlistView().contains(s.bannerId())) throw new EnrollmentException("Already waitlisted");

        if (c.rosterView().size() >= c.capacity()) {
            c.addToWaitlist(s.bannerId());
            courses.save(c);
            enrollments.saveWaitlist(c.code(), s.bannerId());
            return "WAITLIST";
        } else {
            c.addToRoster(s.bannerId());
            courses.save(c);
            enrollments.saveEnrollment(c.code(), s.bannerId());
            return "ENROLLED";
        }
    }

    // Drop a student if needed
    public String drop(String bannerId, String courseCode) {
        Course c = courses.findByCode(courseCode)
                .orElseThrow(() -> new NotFoundException("No course " + courseCode));

        if (c.removeFromRoster(bannerId)) {
            String promoted = c.popWaitlisted();      // FIFO
            if (promoted != null) {
                c.addToRoster(promoted);
                courses.save(c);
                enrollments.remove(courseCode, bannerId);
                enrollments.saveEnrollment(courseCode, promoted);
                return "Dropped & PROMOTED " + promoted;
            } else {
                courses.save(c);
                enrollments.remove(courseCode, bannerId);
                return "Dropped";
            }
        } else if (c.removeFromWaitlist(bannerId)) {
            courses.save(c);
            enrollments.remove(courseCode, bannerId);
            return "Removed from waitlist";
        }
        throw new EnrollmentException("Not enrolled or waitlisted");
    }

    // Queries used by CLI
    public Collection<Student> getStudents() {
        return students.findAll();
    }

    public Collection<Course> getCourses() {
        return courses.findAll();
    }
}

