import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single student record.
 * Demonstrates encapsulation: all fields are private and accessed via getters/setters.
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DELIMITER = ",";

    private int id;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String department;
    private double cgpa;

    public Student(int id, String name, int age, String gender, String address,
                    String department, double cgpa) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.department = department;
        this.cgpa = cgpa;
    }

    // ---------- Getters ----------
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getDepartment() {
        return department;
    }

    public double getCgpa() {
        return cgpa;
    }

    // ---------- Setters (id is intentionally not settable — it's the map key) ----------
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    /**
     * Serializes this student to one CSV line for file storage.
     * Address/name fields are wrapped in quotes and internal commas escaped,
     * so a comma inside an address (e.g. "12 Main St, Springfield") doesn't
     * break column alignment when the file is opened in Excel.
     */
    public String toFileString() {
        return id + DELIMITER
                + csvEscape(name) + DELIMITER
                + age + DELIMITER
                + csvEscape(gender) + DELIMITER
                + csvEscape(address) + DELIMITER
                + csvEscape(department) + DELIMITER
                + cgpa;
    }

    private static String csvEscape(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    /**
     * Parses one CSV line (as produced by toFileString) back into a Student.
     * Returns null if the line is malformed, so callers can skip bad rows.
     */
    public static Student fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        List<String> fields = splitCsvLine(line);
        if (fields.size() != 7) {
            return null;
        }
        try {
            int id = Integer.parseInt(fields.get(0).trim());
            String name = fields.get(1);
            int age = Integer.parseInt(fields.get(2).trim());
            String gender = fields.get(3);
            String address = fields.get(4);
            String department = fields.get(5);
            double cgpa = Double.parseDouble(fields.get(6).trim());
            return new Student(id, name, age, gender, address, department, cgpa);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Splits one CSV line honoring quoted fields (so commas inside a quoted
     * address don't get treated as column separators).
     */
    private static List<String> splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields;
    }

    @Override
    public String toString() {
        return String.format("%-6d %-18s %-4d %-8s %-20s %-12s %-5.2f",
                id, name, age, gender, address, department, cgpa);
    }

    public static String header() {
        return String.format("%-6s %-18s %-4s %-8s %-20s %-12s %-5s",
                "ID", "Name", "Age", "Gender", "Address", "Department", "CGPA");
    }
}
