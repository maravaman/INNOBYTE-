import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class QuizDao {
    private QuestionDao questionDao;

    public QuizDao(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    public boolean addQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, time_limit_minutes, user_id) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, quiz.getTitle());
            pstmt.setInt(2, quiz.getTimeLimitMinutes());
            pstmt.setInt(3, quiz.getUserId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        quiz.setId(generatedKeys.getInt(1)); // Set the generated quiz ID
                        // Now add questions for this quiz
                        for (Question q : quiz.getQuestions()) {
                            q.setQuizId(quiz.getId()); // Link question to the new quiz ID
                            questionDao.addQuestion(q);
                        }
                    }
                }
                System.out.println("Quiz '" + quiz.getTitle() + "' added successfully with ID: " + quiz.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding quiz: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Quiz getQuizById(int id) {
        String sql = "SELECT id, title, time_limit_minutes, user_id FROM quizzes WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Quiz quiz = new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("time_limit_minutes"),
                    rs.getInt("user_id")
                );
                // Also fetch all questions for this quiz
                quiz.setQuestions(questionDao.getQuestionsByQuizId(quiz.getId()));
                return quiz;
            }
        } catch (SQLException e) {
            System.err.println("Error getting quiz by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT id, title, time_limit_minutes, user_id FROM quizzes";
        try (Connection conn = DbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz quiz = new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("time_limit_minutes"),
                    rs.getInt("user_id")
                );
                // Optionally load questions here if needed for direct display,
                // but usually load questions only when taking the quiz.
                // For a list view, just the quiz details are fine.
                quizzes.add(quiz);
            }
            System.out.println("Fetched " + quizzes.size() + " quizzes from DB."); // Debug print
        } catch (SQLException e) {
            System.err.println("Error getting all quizzes: " + e.getMessage());
            e.printStackTrace();
        }
        return quizzes;
    }

    public boolean deleteQuiz(int quizId) {
        // SQLite will handle cascading delete for questions because of ON DELETE CASCADE
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}