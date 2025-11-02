package edu.uca.registration.util;

import java.nio.file.Path;
import java.util.Properties;



// Centralized configuration
// Precedence: JVM system properties > environment variables > defaults.
// Key Value pairs
// Keys:
// - storage.dir (default ".")
// - csv.students (default "students.csv")
// - csv.courses (default "courses.csv")
// - csv.enrollments (default "enrollments.csv")
public final class Config {
    private static final Properties P = new Properties();
    static {
        System.getProperties().forEach((k,v) -> P.put(k,v));
        System.getenv().forEach(P::putIfAbsent);
        P.putIfAbsent("storage.type", "csv");
        P.putIfAbsent("storage.dir", ".");
        P.putIfAbsent("csv.students", "students.csv");
        P.putIfAbsent("csv.courses", "courses.csv");
        P.putIfAbsent("csv.enrollments", "enrollments.csv");
    }
    public static String get(String key){ return P.getProperty(key); }
    public static Path path(String key){ return Path.of(get("storage.dir")).resolve(get(key)); }
}

