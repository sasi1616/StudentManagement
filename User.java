/**
 * Represents one login account.
 *
 * NOTE: the password is stored here, and in users.txt, as plain text —
 * no hashing or encryption is applied. This is simpler to read/debug but
 * is NOT secure: anyone who can open users.txt can read every password
 * directly. Fine for a learning project; do not use this approach for
 * anything that handles real user data.
 */
public class User {

    private final String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Serializes this account to one CSV line: username,password
     */
    public String toFileString() {
        return username + "," + password;
    }

    /**
     * Parses one CSV line (as produced by toFileString) back into a User.
     * Returns null if the line is malformed, so callers can skip bad rows.
     */
    public static User fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split(",", -1);
        if (parts.length != 2) {
            return null;
        }
        String username = parts[0].trim();
        String password = parts[1].trim();
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }
        return new User(username, password);
    }
}
