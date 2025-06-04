import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class QuizAttemptDao {

    public boolean addQuizAttempt(QuizAttempt attempt) {
        String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, score, total_questions, attempt_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, attempt.getUserId());
            pstmt.setInt(2, attempt.getQuizId());
            pstmt.setInt(3, attempt.getScore());
            pstmt.setInt(4, attempt.getTotalQuestions());
            pstmt.setString(5, attempt.getAttemptDate());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        attempt.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding quiz attempt: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<QuizAttempt> getAttemptsByUserId(int userId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        // Join with quizzes and users table to get quiz title and username for display
        String sql = "SELECT qa.id, qa.user_id, qa.quiz_id, qa.score, qa.total_questions, qa.attempt_date, " +
                     "q.title AS quiz_title, u.username " +
                     "FROM quiz_attempts qa " +
                     "JOIN quizzes q ON qa.quiz_id = q.id " +
                     "JOIN users u ON qa.user_id = u.id " +
                     "WHERE qa.user_id = ? ORDER BY qa.attempt_date DESC"; // Order by date, latest first
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                QuizAttempt attempt = new QuizAttempt(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getString("attempt_date")
                );
                attempt.setQuizTitle(rs.getString("quiz_title"));
                attempt.setUsername(rs.getString("username"));
                attempts.add(attempt);
            }
            System.out.println("Fetched " + attempts.size() + " attempts for user ID " + userId + " from DB."); // Debug print
        } catch (SQLException e) {
            System.err.println("Error getting quiz attempts by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return attempts;
    }

    public List<QuizAttempt> getAllQuizAttempts() {
        List<QuizAttempt> attempts = new ArrayList<>();
        // Join with quizzes and users table to get quiz title and username for display
        String sql = "SELECT qa.id, qa.user_id, qa.quiz_id, qa.score, qa.total_questions, qa.attempt_date, " +
                     "q.title AS quiz_title, u.username " +
                     "FROM quiz_attempts qa " +
                     "JOIN quizzes q ON qa.quiz_id = q.id " +
                     "JOIN users u ON qa.user_id = u.id " +
                     "ORDER BY qa.attempt_date DESC"; // Order by date, latest first
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                QuizAttempt attempt = new QuizAttempt(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("quiz_id"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getString("attempt_date")
                );
                attempt.setQuizTitle(rs.getString("quiz_title"));
                attempt.setUsername(rs.getString("username"));
                attempts.add(attempt);
            }
            System.out.println("Fetched " + attempts.size() + " total attempts from DB."); // Debug print
        } catch (SQLException e) {
            System.err.println("Error getting all quiz attempts: " + e.getMessage());
            e.printStackTrace();
        }
        return attempts;
    }
}