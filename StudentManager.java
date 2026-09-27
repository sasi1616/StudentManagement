import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the collection of Student records: add, update, delete, view, search.
 * Backs every mutation with a save to disk (students.csv) so data survives
 * across runs. Uses only core Java (java.io / java.util) — no external
 * libraries — and the CSV output opens directly in Excel/LibreOffice/Sheets.
 *
 * Uses a LinkedHashMap<Integer, Student> so lookups by ID are O(1) while
 * insertion order is preserved for a stable "view all" listing.
 */
public class StudentManager {

    private static final String FILE_NAME = "students.csv";
    private static final String HEADER_LINE = "ID,Name,Age,Gender,Address,Department,CGPA";

    private final Map<Integer, Student> students = new LinkedHashMap<>();

    public StudentManager() {
        loadFromFile();
    }

    /**
     * Adds a new student. Fails if the ID is already in use.
     */
    public boolean addStudent(Student student) {
        if (students.containsKey(student.getId())) {
            return false;
        }
        students.put(student.getId(), student);
        saveToFile();
        return true;
    }

    /**
     * Updates an existing student's mutable fields.
     */
    public boolean updateStudent(int id, String address) {
        Student existing = students.get(id);
        if (existing == null) {
            return false;
        }
        // existing.setName(name);
        // existing.setAge(age);
        // existing.setGender(gender);
        existing.setAddress(address);
        // existing.setDepartment(department);
        // existing.setCgpa(cgpa);
        saveToFile();
        return true;
    }

    public boolean deleteStudent(int id) {
        Student removed = students.remove(id);
        if (removed != null) {
            saveToFile();
            return true;
        }
        return false;
    }

    public Student searchStudent(int id) {
        return students.get(id);
    }

    /**
     * Case-insensitive substring search on name.
     */
    public List<Student> searchByName(String query) {
        List<Student> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Student s : students.values()) {
            if (s.getName().toLowerCase().contains(lowerQuery)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Student> getAllStudents() {
        List<Student> all = new ArrayList<>(students.values());
        all.sort(Comparator.comparingInt(Student::getId));
        return all;
    }

    public int count() {
        return students.size();
    }

    public boolean exists(int id) {
        return students.containsKey(id);
    }

    // ---------------- File Handling (core Java only: java.io) ----------------

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(HEADER_LINE);
            writer.newLine();
            for (Student s : getAllStudents()) {
                writer.write(s.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header row
            while ((line = reader.readLine()) != null) {
                Student s = Student.fromFileString(line);
                if (s != null) {
                    students.put(s.getId(), s);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading from file: " + e.getMessage());
        }
    }
}
