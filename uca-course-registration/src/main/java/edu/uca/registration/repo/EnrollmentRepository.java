package edu.uca.registration.repo;


// Persists enrollment relations
// Business rules are in the service package
public interface EnrollmentRepository {
    void saveEnrollment(String courseCode, String studentId);   // ENROLLED
    void saveWaitlist(String courseCode, String studentId);     // WAITLIST
    void remove(String courseCode, String studentId);

    // Rebuild in-memory roster for courses from the persisted file
    // Called once on startup after courses are loaded
    void loadInto(CourseRepository courses); // reconstruct roster/waitlists
}

