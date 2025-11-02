package edu.uca.registration.repo.csv;

import edu.uca.registration.model.Course;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


// CSV format =  code, title, capacity
// Only course metadata is here, the roster is remade in the CsvEnrollmentRepository

public final class CsvCourseRepository implements CourseRepository {
    private final Path file;

    public CsvCourseRepository(Path file) {
        this.file = file;
    }

    @Override
    public Optional<Course> findByCode(String code) {
        return findAll().stream()
                .filter(c -> c.code().equals(code))
                .findFirst();
    }

    @Override
    public Collection<Course> findAll() {

        if (!Files.exists(file)) return List.of();

        List<Course> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String ln;
            while ((ln = br.readLine()) != null) {

                String[] p = ln.split(",", -1);

                if (p.length >= 3) {
                    try {
                        int cap = Integer.parseInt(p[2].trim());
                        // roster/waitlist reconstructed by EnrollmentRepository
                        out.add(new Course(p[0].trim(), p[1].trim(), cap, null, null));
                    } catch (NumberFormatException nfe) {
                        Log.warn("Bad capacity, skipping course row: " + ln);
                    }
                } else {
                    Log.warn("Bad course row, skipping: " + ln);
                }
            }
        } catch (IOException e) {
            Log.error("Reading " + file + " failed", e);
        }
        return out;
    }

    @Override
    public void save(Course c) {
        // Rewrite the csv
        Map<String, Course> all = new LinkedHashMap<>();
        for (Course each : findAll()) all.put(each.code(), each);
        all.put(c.code(), c);

        try {
            Path parent = file.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
                for (Course course : all.values()) {
                    pw.println(course.code() + "," + course.title() + "," + course.capacity());
                }
            }
        } catch (IOException e) {
            Log.error("Writing " + file + " failed", e);
        }
    }
}

