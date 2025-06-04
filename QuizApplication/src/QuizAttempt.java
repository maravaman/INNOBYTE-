class QuizAttempt {
    private int id;
    private int userId;
    private int quizId;
    private int score;
    private int totalQuestions;
    private String attemptDate; // Stored as TEXT (YYYY-MM-DD HH:MM:SS)

    // Optional: for display purposes, to show quiz title and username
    private String quizTitle;
    private String username;

    public QuizAttempt(int id, int userId, int quizId, int score, int totalQuestions, String attemptDate) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.attemptDate = attemptDate;
    }

    // Constructor without ID (for new attempts)
    public QuizAttempt(int userId, int quizId, int score, int totalQuestions, String attemptDate) {
        this(0, userId, quizId, score, totalQuestions, attemptDate);
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public String getAttemptDate() {
        return attemptDate;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void setAttemptDate(String attemptDate) {
        this.attemptDate = attemptDate;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        // This will be used to display in ListViews. Make it descriptive.
        return String.format("Quiz: %s | User: %s | Score: %d/%d | Date: %s",
                             quizTitle != null ? quizTitle : "N/A",
                             username != null ? username : "N/A",
                             score, totalQuestions, attemptDate);
    }
}