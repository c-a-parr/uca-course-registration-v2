package edu.uca.registration.repo;

import edu.uca.registration.model.Student;
import java.util.*;

// Abstraction for student persistence
// Allows swapping CSV without changing callers.
public interface StudentRepository {
    Optional<Student> findById(String bannerId);
    Collection<Student> findAll();
    void save(Student s);
}

