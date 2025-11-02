package edu.uca.registration.app;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.service.RegistrationService;

import java.util.Scanner;

// Console Ui
// Prints Menu
// Handles user inputs
// Outsources all operations to RegistrationService class
public final class Cli {
    private final RegistrationService svc;
    public Cli(RegistrationService svc){ this.svc = svc; }

    public void run() {
        Scanner sc = new Scanner(System.in);

        // Menu loop
        while (true) {
            System.out.println("\n1) Add student  \n2) Add course  \n3) Enroll  \n4) Drop  \n5) List students  \n6) List courses  \n0) Exit");
            System.out.print("Choose: ");

            // Options for menu
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Banner ID: "); var id = sc.nextLine().trim();
                    System.out.print("Name: "); var nm = sc.nextLine().trim();
                    System.out.print("Email: "); var em = sc.nextLine().trim();
                    try { svc.addStudent(id, nm, em); System.out.println("OK"); }
                    catch (RuntimeException e){ System.out.println(e.getMessage()); }
                }
                case "2" -> {
                    System.out.print("Course Code: "); var code = sc.nextLine().trim();
                    System.out.print("Title: "); var title = sc.nextLine().trim();
                    System.out.print("Capacity: "); var cap = Integer.parseInt(sc.nextLine().trim());
                    try { svc.addCourse(code, title, cap); System.out.println("OK"); }
                    catch (RuntimeException e){ System.out.println(e.getMessage()); }
                }
                case "3" -> {
                    System.out.print("Student ID: "); var sid = sc.nextLine().trim();
                    System.out.print("Course Code: "); var cc = sc.nextLine().trim();
                    try { var msg = svc.enroll(sid, cc); System.out.println(msg); }
                    catch (RuntimeException e){ System.out.println(e.getMessage()); }
                }
                case "4" -> {
                    System.out.print("Student ID: "); var sid = sc.nextLine().trim();
                    System.out.print("Course Code: "); var cc = sc.nextLine().trim();
                    try { var msg = svc.drop(sid, cc); System.out.println(msg); }
                    catch (RuntimeException e){ System.out.println(e.getMessage()); }
                }
                case "5" -> {
                    System.out.println("Students:");
                    for (Student s : svc.getStudents()) {
                        System.out.println(" - " + s.bannerId() + " " + s.name() + " <" + s.email() + ">");
                    }
                }
                case "6" -> {
                    System.out.println("Courses:");
                    for (Course c : svc.getCourses()) {
                        System.out.println(" - " + c.toString());
                    }
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }
}
