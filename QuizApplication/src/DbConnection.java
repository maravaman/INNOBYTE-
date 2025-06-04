import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

class DbConnection {
    private static final String DB_URL = "jdbc:sqlite:quiz_app.db";

    static {
        System.out.println("DbConnection static initializer started.");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Enable foreign key support
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Foreign keys enabled.");

            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                      "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                      "username TEXT NOT NULL UNIQUE," +
                                      "password_hash TEXT NOT NULL" +
                                      ");";
            stmt.execute(createUsersTable);

            // Create quizzes table
            String createQuizzesTable = "CREATE TABLE IF NOT EXISTS quizzes (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "title TEXT NOT NULL," +
                                        "time_limit_minutes INTEGER NOT NULL," +
                                        "user_id INTEGER NOT NULL," + // User who created the quiz (admin)
                                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                                        ");";
            stmt.execute(createQuizzesTable);

            // Create questions table
            String createQuestionsTable = "CREATE TABLE IF NOT EXISTS questions (" +
                                          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                          "quiz_id INTEGER NOT NULL," +
                                          "question_text TEXT NOT NULL," +
                                          "option_a TEXT NOT NULL," +
                                          "option_b TEXT NOT NULL," +
                                          "option_c TEXT NOT NULL," +
                                          "option_d TEXT NOT NULL," +
                                          "correct_option TEXT NOT NULL," +
                                          "FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE" +
                                          ");";
            stmt.execute(createQuestionsTable);

            // Create quiz_attempts table
            String createQuizAttemptsTable = "CREATE TABLE IF NOT EXISTS quiz_attempts (" +
                                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                             "user_id INTEGER NOT NULL," +
                                             "quiz_id INTEGER NOT NULL," +
                                             "score INTEGER NOT NULL," +
                                             "total_questions INTEGER NOT NULL," +
                                             "attempt_date TEXT NOT NULL," + // Store as ISO 8601 string YYYY-MM-DD HH:MM:SS
                                             "FOREIGN KEY (user_id) REFERENCES users(id)," +
                                             "FOREIGN KEY (quiz_id) REFERENCES quizzes(id)" +
                                             ");";
            stmt.execute(createQuizAttemptsTable);

            System.out.println("Database tables checked/created successfully.");

            // Add initial admin user if not exists
            try (PreparedStatement checkAdmin = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username = 'admin'");
                 ResultSet rs = checkAdmin.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String adminPassword = "adminpass";
                    String hashedAdminPassword = BCrypt.hashpw(adminPassword, BCrypt.gensalt()); // Hash the password

                    try (PreparedStatement insertAdmin = conn.prepareStatement("INSERT INTO users (username, password_hash) VALUES (?, ?)")) {
                        insertAdmin.setString(1, "admin");
                        insertAdmin.setString(2, hashedAdminPassword); // Store the hashed password
                        insertAdmin.executeUpdate();
                        System.out.println("Default admin user added with HASHED password.");
                    }
                } else {
                    System.out.println("Admin user already exists. Not re-adding.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DbConnection static initializer finished.");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}