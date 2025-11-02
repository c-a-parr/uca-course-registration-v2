package edu.uca.registration.repo.csv;

import edu.uca.registration.model.Student;
import edu.uca.registration.repo.StudentRepository;
import edu.uca.registration.util.Log;

import java.io.*;
import java.nio.file.*;
import java.util.*;

// CSV format = banner, name, email
// Simple, safe implementation: rewrites entire file on save

public final class CsvStudentRepository implements StudentRepository {
    private final Path file;

    public CsvStudentRepository(Path file) { this.file = file; }

    @Override public Optional<Student> findById(String id) {
        return findAll().stream().filter(s -> s.bannerId().equals(id)).findFirst();
    }

    @Override public Collection<Student> findAll() {
        if (!Files.exists(file)) return List.of();

        List<Student> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(file)) {

            for (String ln; (ln = br.readLine()) != null; ) {
                String[] p = ln.split(",", -1);
                if (p.length >= 3) out.add(new Student(p[0], p[1], p[2]));
                else Log.warn("Bad student row skipped: " + ln);
            }

        } catch (IOException e) { Log.error("Reading " + file + " failed", e); }
        return out;
    }

    @Override public void save(Student s) {
        // read all, replace/insert, rewrite file
        try {
            Files.createDirectories(file.getParent() == null ? Path.of(".") : file.getParent());
            Map<String, Student> all = new LinkedHashMap<>();

            for (Student each : findAll()) all.put(each.bannerId(), each);
            all.put(s.bannerId(), s);

            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
                for (Student st : all.values())
                    pw.println(st.bannerId() + "," + st.name() + "," + st.email());
            }
        } catch (IOException e) { Log.error("Writing " + file + " failed", e); }
    }
}
