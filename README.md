# UCA-course-registration-v2
An improved version of the original project with automated tests.

# UCA Course Registration (Refactored now with testing enabled)
A console app that manages students, courses, enrollments, and a queued waitlist that has been refactored to a clean, layered architecture.

## Features
- Add/List Students and Courses
- Enroll (with caps and waitlist)
- Drop (Promotes first waitlisted)
- CSV persistence

## Running 
- Our recommendation is to simply download the files and run it in an IDE.
- However it will run from the command line and there are a few ways to do it. We'll list those right below here. Make sure you're in the correct directory as well.
- Bash Using Maven: mvn -q -DskipTests clean package
     java -jar target/course-registration-1.0.0.jar
- Bash without Maven: java -jar target/course-registration-1.0.0.jar --demo
- Plain Java: mkdir -p out javac -d out $(find src/main/java -name "*.java") java -cp out edu.uca.registration.Main
- One alternative that worked for me in case all other terminal approaches fail: java -jar target/$(ls target | grep '\.jar$')

## Configuration
Paths come from system properties/env
- storage.dir (.)
- csv.students (default: students.csv)
- csv.courses (default: courses.csv)
- csv.enrollments (default: enrollments.csv)

## Data Formats (CSV)
- students.csv: bannerId,name,email
- courses.csv: code,title,capacity
- enrollments.csv: courseCode|studentId|ENROLLED or WAITLIST

**Note: On startup, enrollments are replayed to rebuild rosters/waitlists (FIFO preserved).**
