package edu.uca.registration.repo.csv;

import edu.uca.registration.model.Course;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.EnrollmentRepository;
import edu.uca.registration.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * CSV format is pipe delimited
 *
 *   courseCode|studentId|ENROLLED
 *
 *   courseCode|studentId|WAITLIST
 *
 * This class ignores relationships while the service package enforces rules
 */

public final class CsvEnrollmentRepository implements EnrollmentRepository {
    private final Path file;

    public CsvEnrollmentRepository(Path file) {
        this.file = file;
    }

    private static final String ENROLLED = "ENROLLED";
    private static final String WAITLIST = "WAITLIST";

    // Load all rows from file while skipping bad lines
    private List<Record> readAll() {
        if (!Files.exists(file)) return List.of();
        List<Record> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split("\\|", -1);
                if (p.length >= 3) {
                    String code = p[0].trim();
                    String sid  = p[1].trim();
                    String st   = p[2].trim().toUpperCase(Locale.ROOT);
                    if (ENROLLED.equals(st) || WAITLIST.equals(st)) {
                        out.add(new Record(code, sid, st));
                    } else {
                        Log.warn("Unknown status '" + st + "', skipping row: " + ln);
                    }
                } else {
                    Log.warn("Bad enrollment row, skipping: " + ln);
                }
            }
        } catch (IOException e) {
            Log.error("Reading " + file + " failed", e);
        }
        return out;
    }

    //Re-write full file from provided records, create files or directories if needed
    private void writeAll(Collection<Record> records) {
        try {
            Path parent = file.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
                for (Record r : records) {
                    pw.println(r.courseCode + "|" + r.studentId + "|" + r.status);
                }
            }
        } catch (IOException e) {
            Log.error("Writing " + file + " failed", e);
        }
    }

    @Override
    public void saveEnrollment(String courseCode, String studentId) {
        List<Record> all = new ArrayList<>(readAll());
        // remove any previous state for this (course, student) then add ENROLLED
        all.removeIf(r -> r.courseCode.equals(courseCode) && r.studentId.equals(studentId));
        all.add(new Record(courseCode, studentId, ENROLLED));
        writeAll(all);
    }

    @Override
    public void saveWaitlist(String courseCode, String studentId) {
        List<Record> all = new ArrayList<>(readAll());
        all.removeIf(r -> r.courseCode.equals(courseCode) && r.studentId.equals(studentId));
        all.add(new Record(courseCode, studentId, WAITLIST));
        writeAll(all);
    }

    @Override
    public void remove(String courseCode, String studentId) {
        List<Record> all = new ArrayList<>(readAll());
        boolean changed = all.removeIf(r -> r.courseCode.equals(courseCode) && r.studentId.equals(studentId));
        if (changed) writeAll(all);
    }


     // Reconstruct in-memory roster & waitlist for courses already loaded by CourseRepository
     // Call this once on startup after courses have been loaded and saved
    @Override
    public void loadInto(CourseRepository courses) {

        // Create a quick lookup of in-memory course objects
        Map<String, Course> byCode = new LinkedHashMap<>();
        for (Course c : courses.findAll()) byCode.put(c.code(), c);

        // Replay enrollments to rebuild roster/waitlist
        for (Record r : readAll()) {
            Course c = byCode.get(r.courseCode);
            if (c == null) {
                Log.warn("Enrollment references unknown course '" + r.courseCode + "', skipping");
                continue;
            }
            if (ENROLLED.equals(r.status)) {
                c.addToRoster(r.studentId);
            } else {
                c.addToWaitlist(r.studentId); // FIFO order preserved by file order
            }
        }

        // Persist any mutations to keep repository state aligned
        for (Course c : byCode.values()) courses.save(c);
    }

    // Simple value object for one enrollment row
    private static final class Record {
        final String courseCode;
        final String studentId;
        final String status;

        Record(String courseCode, String studentId, String status) {
            this.courseCode = courseCode;
            this.studentId = studentId;
            this.status = status;
        }
    }
}
