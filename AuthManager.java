import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages login accounts: authentication and password changes, persisted to
 * users.txt as plain text (see the note in User.java about the security
 * trade-off of this approach).
 *
 * On first run (no users.txt present), a single default admin account is
 * created automatically so the app is usable out of the box.
 */
public class AuthManager {

    private static final String FILE_NAME = "users.txt";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    private final Map<String, User> users = new LinkedHashMap<>();

    public AuthManager() {
        loadFromFile();
        if (users.isEmpty()) {
            createDefaultAdmin();
        }
    }

    private void createDefaultAdmin() {
        User admin = new User(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        users.put(admin.getUsername(), admin);
        saveToFile();
        System.out.println("No existing accounts found — created a default login:");
        System.out.println("  Username: " + DEFAULT_USERNAME);
        System.out.println("  Password: " + DEFAULT_PASSWORD);
        System.out.println("Please log in and change this password right away.");
    }

    /**
     * Checks a username/password pair against the stored value.
     */
    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    /**
     * Changes a user's password after verifying their current one.
     * Returns false if the username doesn't exist or the current
     * password doesn't match.
     */
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        if (!user.getPassword().equals(currentPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        saveToFile();
        return true;
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    // ---------------- File Handling (core Java only: java.io) ----------------

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User u : users.values()) {
                writer.write(u.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users file: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User u = User.fromFileString(line);
                if (u != null) {
                    users.put(u.getUsername(), u);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users file: " + e.getMessage());
        }
    }
}
