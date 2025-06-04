class Question {
    private int id;
    private int quizId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // Stores 'A', 'B', 'C', or 'D'

    public Question(int id, int quizId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    // Constructor without ID (for new questions)
    public Question(int quizId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this(0, quizId, questionText, optionA, optionB, optionC, optionD, correctOption); // ID will be set by DB
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption;
    }

    @Override
    public String toString() {
        return "Question: " + questionText + "\n" +
               "A) " + optionA + "\n" +
               "B) " + optionB + "\n" +
               "C) " + optionC + "\n" +
               "D) " + optionD + "\n" +
               "Correct: " + correctOption;
    }
}
