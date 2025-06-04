import java.util.List;

class QuizManager {
    private QuizDao quizDao;
    private QuestionDao questionDao;
    private QuizAttemptDao quizAttemptDao;

    public QuizManager(QuizDao quizDao, QuestionDao questionDao, QuizAttemptDao quizAttemptDao) {
        this.quizDao = quizDao;
        this.questionDao = questionDao;
        this.quizAttemptDao = quizAttemptDao;
    }

    // Quiz Operations
    public boolean createQuiz(Quiz quiz) {
        return quizDao.addQuiz(quiz);
    }

    public Quiz getQuizById(int id) {
        return quizDao.getQuizById(id);
    }

    public List<Quiz> getAllQuizzes() {
        return quizDao.getAllQuizzes();
    }

    public boolean deleteQuiz(int quizId) {
        return quizDao.deleteQuiz(quizId);
    }

    // Question Operations
    public boolean addQuestion(Question question) {
        return questionDao.addQuestion(question);
    }

    public List<Question> getQuestionsByQuizId(int quizId) {
        return questionDao.getQuestionsByQuizId(quizId);
    }

    // Quiz Attempt Operations
    public boolean recordQuizAttempt(QuizAttempt attempt) {
        return quizAttemptDao.addQuizAttempt(attempt);
    }

    public List<QuizAttempt> getUserQuizAttempts(int userId) {
        return quizAttemptDao.getAttemptsByUserId(userId);
    }

    public List<QuizAttempt> getAllQuizAttempts() {
        return quizAttemptDao.getAllQuizAttempts();
    }

    // Getters for DAOs if needed elsewhere (though manager pattern often abstracts this)
    public QuizDao getQuizDao() {
        return quizDao;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

    public QuizAttemptDao getQuizAttemptDao() {
        return quizAttemptDao;
    }
}