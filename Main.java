import java.io.Console;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Student Record Management System.
 * Requires a successful login before the main menu is shown, and offers a
 * "Change Password" option once logged in. Student data is persisted to
 * students.csv; login accounts to users.txt. **/

public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();
    private static final AuthManager authManager = new AuthManager();

    private static String currentUser;

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" STUDENT RECORD MANAGEMENT SYSTEM");
        System.out.println("=========================================");

        if (!login()) {
            System.out.println("Too many failed login attempts. Exiting.");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> searchStudent();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> searchByName();
                case 7 -> changePassword();
                case 0 -> {
                    running = false;
                    System.out.println("Logged out successfully.");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    // ---------------- Authentication ----------------

    private static boolean login() {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.println("\n-- Login (attempt " + attempt + " of " + MAX_LOGIN_ATTEMPTS + ") --");
            String username = readString("Username: ");
            String password = readPassword("Password: ");
            System.out.println("username:"+username);
            System.out.println("password:"+password);
            if (authManager.authenticate(username, password)) {
                currentUser = username;
                System.out.println("Login successful. Welcome, " + username + "!");
                return true;
            }
            System.out.println("Invalid username or password.");
        }
        return false;
    }

    private static void changePassword() {
        System.out.println("\n-- Change Password --");
        String currentPassword = readPassword("Enter current password: ");
        String newPassword = readPassword("Enter new password: ");
        String confirmPassword = readPassword("Confirm new password: ");

        if (newPassword.length() < 6) {
            System.out.println("New password must be at least 6 characters long.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New password and confirmation do not match.");
            return;
        }

        boolean changed = authManager.changePassword(currentUser, currentPassword, newPassword);
        System.out.println(changed ? "Password changed successfully."
                : "Current password is incorrect. Password not changed.");
    }

    // ---------------- Student menu actions ----------------

    private static void printMenu() {
        System.out.println("\n----------- MENU -----------");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Search Student by Name");
        System.out.println("7. Change Password");
        System.out.println("0. Logout");
        System.out.println("-----------------------------");
    }

    private static void addStudent() {
        System.out.println("\n-- Add Student --");
        int id = readInt("Enter ID: ");
        if (manager.exists(id)) {
            System.out.println("A student with ID " + id + " already exists.");
            return;
        }
        String name = readString("Enter Name: ");
        int age = readInt("Enter Age: ");
        String gender = readString("Enter Gender: ");
        String address = readString("Enter Address: ");
        String department = readString("Enter Department: ");
        double cgpa = readDouble("Enter CGPA: ");

        Student student = new Student(id, name, age, gender, address, department, cgpa);
        boolean added = manager.addStudent(student);
        System.out.println(added ? "Student added successfully." : "Failed to add student.");
    }

    private static void viewAllStudents() {
        System.out.println("\n-- All Students --");
        List<Student> all = manager.getAllStudents();
        if (all.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        System.out.println(Student.header());
        for (Student s : all) {
            System.out.println(s);
        }
        System.out.println("Total: " + all.size() + " student(s).");
    }

    private static void searchStudent() {
        System.out.println("\n-- Search Student by ID --");
        int id = readInt("Enter ID: ");
        Student found = manager.searchStudent(id);
        if (found == null) {
            System.out.println("No student found with ID " + id + ".");
        } else {
            System.out.println(Student.header());
            System.out.println(found);
        }
    }

    private static void searchByName() {
        System.out.println("\n-- Search Student by Name --");
        String query = readString("Enter name (or part of it): ");
        List<Student> results = manager.searchByName(query);
        if (results.isEmpty()) {
            System.out.println("No matching students found.");
            return;
        }
        System.out.println(Student.header());
        for (Student s : results) {
            System.out.println(s);
        }
    }

    private static void updateStudent() {
        System.out.println("\n-- Update Student --");
        int id = readInt("Enter ID of student to update: ");
        Student existing = manager.searchStudent(id);
        if (existing == null) {
            System.out.println("No student found with ID " + id + ".");
            return;
        }
        System.out.println("Current details: " + existing);
        String address = readString("Enter new Address: ");
        boolean updated = manager.updateStudent(id,address);
       // boolean updated = manager.updateStudent(id, name, age, gender, address, department, cgpa);
        System.out.println(updated ? "Student updated successfully." : "Failed to update student.");
    }

    private static void deleteStudent() {
        System.out.println("\n-- Delete Student --");
        int id = readInt("Enter ID of student to delete: ");
        boolean deleted = manager.deleteStudent(id);
        System.out.println(deleted ? "Student deleted successfully." : "No student found with ID " + id + ".");
    }

    // ---------------- Input Helpers ----------------

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a decimal value (e.g. 8.5).");
            }
        }
    }

    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    /**
     * Reads a password, masking the input when the JVM has a real console
     * attached (java.io.Console — core Java, no library needed). Some
     * environments (many IDEs, piped input) don't provide a console, in
     * which case there's no portable way to mask input with core Java
     * alone, so it falls back to plain, visible input via Scanner.
     */
    private static String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword(prompt);

            return chars == null ? "" : new String(chars);
        }
        System.out.print(prompt + "(input will be visible - no console detected) ");
        return scanner.nextLine().trim();
    }
}
