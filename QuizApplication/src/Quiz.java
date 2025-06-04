import java.util.ArrayList;
import java.util.List;

class Quiz {
    private int id;
    private String title;
    private int timeLimitMinutes;
    private int userId; // ID of the admin user who created it
    private List<Question> questions; // To hold questions when retrieving a full quiz

    public Quiz(int id, String title, int timeLimitMinutes, int userId) {
        this.id = id;
        this.title = title;
        this.timeLimitMinutes = timeLimitMinutes;
        this.userId = userId;
        this.questions = new ArrayList<>(); // Initialize the list
    }

    // Constructor without ID (for new quizzes)
    public Quiz(String title, int timeLimitMinutes, int userId) {
        this(0, title, timeLimitMinutes, userId); // ID will be set by DB
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public int getUserId() {
        return userId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        }
        this.questions.add(question);
    }

    @Override
    public String toString() {
        return "Quiz ID: " + id + ", Title: " + title + ", Time Limit: " + timeLimitMinutes + " mins";
    }
}