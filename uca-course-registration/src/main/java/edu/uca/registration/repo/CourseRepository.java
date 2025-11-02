package edu.uca.registration.repo;

import edu.uca.registration.model.Course;
import java.util.*;


// Abstraction for course persistence
public interface CourseRepository {
    Optional<Course> findByCode(String code);
    Collection<Course> findAll();
    void save(Course c);                 // upsert
}
