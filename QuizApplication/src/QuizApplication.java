import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class QuizApplication extends Application {

    private Stage primaryStage;
    private AuthService authService;
    private UserDao userDao;
    private QuizManager quizManager;
    private User currentUser; // Stores the currently logged-in user

    private ScheduledExecutorService timerService;
    private int timeLeftSeconds;
    private Label timerLabel;
    private ProgressIndicator timerProgress; // For visual timer feedback


    @Override
public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;

    // Initialize DAOs and Services
    userDao = new UserDao();
    QuestionDao questionDao = new QuestionDao();
    QuizAttemptDao quizAttemptDao = new QuizAttemptDao();
    quizManager = new QuizManager(new QuizDao(questionDao), questionDao, quizAttemptDao);
    authService = new AuthService(userDao);

    try {
        Class.forName("DbConnection");
        System.out.println("Database connection initialized.");
    } catch (ClassNotFoundException e) {
        System.err.println("Failed to load DbConnection class: " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }

    primaryStage.setTitle("Quiz Application");
    Scene loginScene = createLoginScene();
    applyCss(loginScene); // Apply CSS to the login scene
    primaryStage.setScene(loginScene);
    primaryStage.show();
}

    // --- Scene Creation Methods ---

  private Scene createLoginScene() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));
    grid.getStyleClass().add("form-container"); // Apply style class

    // Wrap grid in a VBox or StackPane to apply background to the whole scene
    VBox root = new VBox();
    root.setAlignment(Pos.CENTER);
    root.getChildren().add(grid);
    root.getStyleClass().add("scene-background"); // Apply background image

    Label usernameLabel = new Label("Username:");
    // No specific styling for these labels for now, will inherit from root or default
    grid.add(usernameLabel, 0, 1);

    TextField usernameField = new TextField();
    usernameField.setPromptText("username");
    grid.add(usernameField, 1, 1);

    Label passwordLabel = new Label("Password:");
    grid.add(passwordLabel, 0, 2);

    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("password");
    grid.add(passwordField, 1, 2);

    Button loginButton = new Button("Login");
    Button registerButton = new Button("Register");

    HBox hbButtons = new HBox(10);
    hbButtons.setAlignment(Pos.BOTTOM_RIGHT);
    hbButtons.getChildren().addAll(loginButton, registerButton);
    grid.add(hbButtons, 1, 4);

    Label messageLabel = new Label();
    grid.add(messageLabel, 1, 5);

    // ... (rest of the loginButton and registerButton action handlers) ...

    loginButton.setOnAction(e -> {
        String username = usernameField.getText();
        String password = passwordField.getText();
        currentUser = authService.login(username, password);
        if (currentUser != null) {
            messageLabel.setText("Login Successful!");
            Scene nextScene;
            if ("admin".equals(currentUser.getUsername())) {
                nextScene = createAdminMenuScene();
            } else {
                nextScene = createUserMenuScene();
            }
            applyCss(nextScene); // Apply CSS to the next scene
            primaryStage.setScene(nextScene);
        } else {
            messageLabel.setText("Invalid Username or Password.");
        }
    });

    registerButton.setOnAction(e -> {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and Password cannot be empty.");
            return;
        }
        if (authService.register(username, password)) {
            messageLabel.setText("Registration Successful! Please Login.");
            usernameField.clear();
            passwordField.clear();
        } else {
            messageLabel.setText("Username already exists. Please choose another.");
        }
    });

    return new Scene(root, 450, 350); // Increased size slightly
}

    private Scene createAdminMenuScene() {
    VBox root = new VBox(20);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(20));
    root.getStyleClass().add("scene-background"); // Apply background

    Label welcomeLabel = new Label("Welcome, Admin!");
    welcomeLabel.getStyleClass().add("header-label"); // Apply header style

    Button createQuizButton = new Button("Create New Quiz");
    createQuizButton.setMaxWidth(Double.MAX_VALUE);
    Button viewAllQuizzesButton = new Button("View All Quizzes");
    viewAllQuizzesButton.setMaxWidth(Double.MAX_VALUE);
    Button viewAllAttemptsButton = new Button("View All Quiz Attempts");
    viewAllAttemptsButton.setMaxWidth(Double.MAX_VALUE);
    Button logoutButton = new Button("Logout");
    logoutButton.setMaxWidth(Double.MAX_VALUE);

    createQuizButton.setOnAction(e -> {
        Scene nextScene = createQuizCreationScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });
    viewAllQuizzesButton.setOnAction(e -> {
        Scene nextScene = createViewAllQuizzesScene(true);
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });
    viewAllAttemptsButton.setOnAction(e -> {
        Scene nextScene = createViewAllAttemptsScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });
    logoutButton.setOnAction(e -> {
        currentUser = null;
        Scene nextScene = createLoginScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });

    root.getChildren().addAll(welcomeLabel, createQuizButton, viewAllQuizzesButton, viewAllAttemptsButton, logoutButton);
    return new Scene(root, 550, 500); // Increased size
}

  private Scene createUserMenuScene() {
    VBox root = new VBox(20);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(20));
    root.getStyleClass().add("scene-background"); // Apply background

    Label welcomeLabel = new Label("Welcome, " + currentUser.getUsername() + "!");
    welcomeLabel.getStyleClass().add("header-label"); // Apply header style

    Button takeQuizButton = new Button("Take a Quiz");
    takeQuizButton.setMaxWidth(Double.MAX_VALUE);
    Button viewMyAttemptsButton = new Button("View My Quiz Attempts");
    viewMyAttemptsButton.setMaxWidth(Double.MAX_VALUE);
    Button logoutButton = new Button("Logout");
    logoutButton.setMaxWidth(Double.MAX_VALUE);

    takeQuizButton.setOnAction(e -> {
        Scene nextScene = createTakeQuizScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });
    viewMyAttemptsButton.setOnAction(e -> {
        Scene nextScene = createViewMyAttemptsScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });
    logoutButton.setOnAction(e -> {
        currentUser = null;
        Scene nextScene = createLoginScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });

    root.getChildren().addAll(welcomeLabel, takeQuizButton, viewMyAttemptsButton, logoutButton);
    return new Scene(root, 550, 450); // Increased size
}
private Scene createQuizCreationScene() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.TOP_LEFT);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25));
    grid.getStyleClass().add("form-container"); // Apply style class

    VBox root = new VBox();
    root.setAlignment(Pos.TOP_LEFT);
    root.getChildren().add(grid);
    root.getStyleClass().add("scene-background"); // Apply background

    Label sceneHeader = new Label("Create New Quiz"); // New label for scene header
    sceneHeader.getStyleClass().add("header-label"); // Apply header style
    grid.add(sceneHeader, 0, 0, 2, 1); // Span across two columns


    Label titleLabel = new Label("Quiz Title:");
    TextField titleField = new TextField();
    titleField.setPromptText("Enter quiz title");

    Label timeLimitLabel = new Label("Time Limit (minutes):");
    TextField timeLimitField = new TextField();
    timeLimitField.setPromptText("e.g., 30");

    Button addQuestionButton = new Button("Add Question");
    Button saveQuizButton = new Button("Save Quiz");
    Button backButton = new Button("Back to Admin Menu");

    VBox questionsContainer = new VBox(15);
    questionsContainer.setPadding(new Insets(10, 0, 0, 0));
    ScrollPane scrollPane = new ScrollPane(questionsContainer);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefHeight(250);

    List<Question> questionsToSave = new ArrayList<>();

    addQuestionButton.setOnAction(e -> {
        VBox questionBox = new VBox(10);
        questionBox.setPadding(new Insets(10));
        questionBox.getStyleClass().add("question-box"); // Apply question box style

        TextArea questionTextArea = new TextArea();
        questionTextArea.setPromptText("Enter question text");
        questionTextArea.setWrapText(true);
        questionTextArea.setPrefRowCount(2);

        TextField optionAField = new TextField();
        optionAField.setPromptText("Option A");
        TextField optionBField = new TextField();
        optionBField.setPromptText("Option B");
        TextField optionCField = new TextField();
        optionCField.setPromptText("Option C");
        TextField optionDField = new TextField();
        optionDField.setPromptText("Option D");

        ComboBox<String> correctOptionComboBox = new ComboBox<>(FXCollections.observableArrayList("A", "B", "C", "D"));
        correctOptionComboBox.setPromptText("Correct Option");
        correctOptionComboBox.setMaxWidth(Double.MAX_VALUE); // Make it stretch

        Button removeQuestionButton = new Button("Remove");
        removeQuestionButton.setOnAction(event -> {
            questionsContainer.getChildren().remove(questionBox);
            questionsToSave.removeIf(q -> q.getQuestionText().equals(questionTextArea.getText()));
        });

        questionBox.getChildren().addAll(
            new Label("Question Text:"), questionTextArea,
            new Label("Option A:"), optionAField,
            new Label("Option B:"), optionBField,
            new Label("Option C:"), optionCField,
            new Label("Option D:"), optionDField,
            new Label("Correct Option:"), correctOptionComboBox,
            removeQuestionButton
        );
        questionsContainer.getChildren().add(questionBox);
    });

    saveQuizButton.setOnAction(e -> {
        String title = titleField.getText().trim();
        String timeLimitStr = timeLimitField.getText().trim();

        if (title.isEmpty() || timeLimitStr.isEmpty() || questionsContainer.getChildren().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all quiz details and add at least one question.");
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitStr);
            if (timeLimit <= 0) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Time limit must be a positive number.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Time limit must be a valid number.");
            return;
        }

        questionsToSave.clear();
        for (javafx.scene.Node node : questionsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox questionBox = (VBox) node;
                ObservableList<javafx.scene.Node> children = questionBox.getChildren();

                TextArea qText = (TextArea) children.get(1);
                TextField optA = (TextField) children.get(3);
                TextField optB = (TextField) children.get(5);
                TextField optC = (TextField) children.get(7);
                TextField optD = (TextField) children.get(9);
                ComboBox<String> correctOpt = (ComboBox<String>) children.get(11);

                if (qText.getText().trim().isEmpty() || optA.getText().trim().isEmpty() ||
                    optB.getText().trim().isEmpty() || optC.getText().trim().isEmpty() ||
                    optD.getText().trim().isEmpty() || correctOpt.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "All question fields and correct options must be filled.");
                    return;
                }

                questionsToSave.add(new Question(
                    0,
                    qText.getText().trim(),
                    optA.getText().trim(),
                    optB.getText().trim(),
                    optC.getText().trim(),
                    optD.getText().trim(),
                    correctOpt.getValue()
                ));
            }
        }

        Quiz newQuiz = new Quiz(title, timeLimit, currentUser.getId());
        newQuiz.setQuestions(questionsToSave);

        if (quizManager.createQuiz(newQuiz)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz '" + title + "' created successfully!");
            titleField.clear();
            timeLimitField.clear();
            questionsContainer.getChildren().clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create quiz.");
        }
    });

    backButton.setOnAction(e -> {
        Scene nextScene = createAdminMenuScene();
        applyCss(nextScene);
        primaryStage.setScene(nextScene);
    });

    // Grid positions adjusted due to new header label
    grid.add(titleLabel, 0, 1);
    grid.add(titleField, 1, 1);
    grid.add(timeLimitLabel, 0, 2);
    grid.add(timeLimitField, 1, 2);
    grid.add(addQuestionButton, 0, 3);
    grid.add(scrollPane, 0, 4, 2, 1);
    grid.add(saveQuizButton, 0, 5);
    grid.add(backButton, 1, 5);

    return new Scene(root, 750, 700); // Increased size
}
private Scene createViewAllQuizzesScene(boolean isAdmin) {
    VBox root = new VBox(10);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.TOP_LEFT);
    root.getStyleClass().add("scene-background"); // Apply background

    Label headerLabel = new Label("All Available Quizzes");
    headerLabel.getStyleClass().add("header-label"); // Apply header style

    ListView<Quiz> quizListView = new ListView<>();
    quizListView.getStyleClass().add("list-view"); // Apply list view style
    ObservableList<Quiz> quizzes = FXCollections.observableArrayList(quizManager.getAllQuizzes());
    quizListView.setItems(quizzes);
    System.out.println("Quizzes loaded into UI: " + quizzes.size());

    Button backButton = new Button("Back");
    backButton.setOnAction(e -> {
        Scene nextScene;
        if (isAdmin) {
            nextScene = createAdminMenuScene();
        } else {
            nextScene = createUserMenuScene();
        }
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });

    root.getChildren().addAll(headerLabel, quizListView);

    if (isAdmin) {
        Button deleteQuizButton = new Button("Delete Selected Quiz");
        deleteQuizButton.setOnAction(e -> {
            Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
            if (selectedQuiz != null) {
                Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete quiz: \"" + selectedQuiz.getTitle() + "\"?",
                        ButtonType.YES, ButtonType.NO);
                confirmDelete.setTitle("Confirm Deletion");
                Optional<ButtonType> result = confirmDelete.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    if (quizManager.deleteQuiz(selectedQuiz.getId())) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz deleted successfully.");
                        quizzes.remove(selectedQuiz);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete quiz.");
                    }
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a quiz to delete.");
            }
        });
        HBox buttons = new HBox(10, deleteQuizButton, backButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().add(buttons);
    } else {
        root.getChildren().add(backButton);
    }

    return new Scene(root, 650, 550); // Increased size
}

   private Scene createTakeQuizScene() {
    VBox root = new VBox(10);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.TOP_LEFT);
    root.getStyleClass().add("scene-background"); // Apply background

    Label headerLabel = new Label("Select a Quiz to Take");
    headerLabel.getStyleClass().add("header-label"); // Apply header style

    ListView<Quiz> quizSelectionListView = new ListView<>();
    quizSelectionListView.getStyleClass().add("list-view"); // Apply list view style
    ObservableList<Quiz> availableQuizzes = FXCollections.observableArrayList(quizManager.getAllQuizzes());
    quizSelectionListView.setItems(availableQuizzes);

    Button takeSelectedQuizButton = new Button("Take Selected Quiz");
    Button backButton = new Button("Back to User Menu");

    takeSelectedQuizButton.setOnAction(e -> {
        Quiz selectedQuiz = quizSelectionListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            Scene nextScene = createQuizTakingScene(selectedQuiz);
            applyCss(nextScene); // Apply CSS
            primaryStage.setScene(nextScene);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Quiz Selected", "Please select a quiz from the list to take.");
        }
    });

    backButton.setOnAction(e -> {
        Scene nextScene = createUserMenuScene();
        applyCss(nextScene); // Apply CSS
        primaryStage.setScene(nextScene);
    });

    HBox buttons = new HBox(10);
    buttons.setAlignment(Pos.BOTTOM_RIGHT);
    buttons.getChildren().addAll(takeSelectedQuizButton, backButton);

    root.getChildren().addAll(headerLabel, quizSelectionListView, buttons);
    return new Scene(root, 650, 550); // Increased size
}

  private Scene createQuizTakingScene(Quiz quiz) {
    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.TOP_LEFT);
    root.getStyleClass().add("scene-background"); // Apply background

    Label quizTitleLabel = new Label("Quiz: " + quiz.getTitle());
    quizTitleLabel.getStyleClass().add("header-label"); // Apply header style

    timerLabel = new Label("Time: --:--");
    timerLabel.setId("timer-label"); // Apply ID for specific styling
    timerProgress = new ProgressIndicator(1.0);
    timerProgress.setPrefSize(30, 30);
    timerProgress.getStyleClass().add("progress-indicator"); // Apply progress indicator style

    HBox timerBox = new HBox(10, timerLabel, timerProgress);
    timerBox.setAlignment(Pos.CENTER_RIGHT);
    timerBox.setPadding(new Insets(0, 0, 10, 0));

    List<Question> questions = quizManager.getQuestionDao().getQuestionsByQuizId(quiz.getId());
    if (questions.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "No Questions", "This quiz has no questions. Returning to user menu.");
        Scene nextScene = createUserMenuScene();
        applyCss(nextScene);
        return nextScene;
    }
    Collections.shuffle(questions);

    List<String> userAnswers = new ArrayList<>(Collections.nCopies(questions.size(), null));

    VBox questionsContainer = new VBox(25);
    questionsContainer.setPadding(new Insets(10));
    ScrollPane scrollPane = new ScrollPane(questionsContainer);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefHeight(400);

    ToggleGroup[] toggleGroups = new ToggleGroup[questions.size()];

    for (int i = 0; i < questions.size(); i++) {
        Question q = questions.get(i);
        VBox questionBox = new VBox(5);
        questionBox.getStyleClass().add("question-box"); // Apply question box style

        Label questionTextLabel = new Label((i + 1) + ". " + q.getQuestionText());
        questionTextLabel.setWrapText(true);
        questionTextLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;"); // Direct style for question text

        toggleGroups[i] = new ToggleGroup();

        RadioButton rbA = new RadioButton("A) " + q.getOptionA());
        rbA.setToggleGroup(toggleGroups[i]);
        rbA.getStyleClass().add("radio-button"); // Apply radio button style
        RadioButton rbB = new RadioButton("B) " + q.getOptionB());
        rbB.setToggleGroup(toggleGroups[i]);
        rbB.getStyleClass().add("radio-button");
        RadioButton rbC = new RadioButton("C) " + q.getOptionC());
        rbC.setToggleGroup(toggleGroups[i]);
        rbC.getStyleClass().add("radio-button");
        RadioButton rbD = new RadioButton("D) " + q.getOptionD());
        rbD.setToggleGroup(toggleGroups[i]);
        rbD.getStyleClass().add("radio-button");

        final int questionIndex = i;
        toggleGroups[i].selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                String answer = selected.getText().substring(0, 1);
                userAnswers.set(questionIndex, answer);
            } else {
                userAnswers.set(questionIndex, null);
            }
        });

        questionBox.getChildren().addAll(questionTextLabel, rbA, rbB, rbC, rbD);
        questionsContainer.getChildren().add(questionBox);
    }

    Button submitButton = new Button("Submit Quiz");
    submitButton.setOnAction(e -> {
        stopTimer();
        calculateAndRecordScore(quiz, questions, userAnswers);
        Scene nextScene = createUserMenuScene();
        applyCss(nextScene);
        primaryStage.setScene(nextScene);
    });

    root.getChildren().addAll(quizTitleLabel, timerBox, scrollPane, submitButton);

    timeLeftSeconds = quiz.getTimeLimitMinutes() * 60;
    startTimer(quiz, questions, userAnswers);

    return new Scene(root, 750, 750); // Increased size
}

    private void startTimer(Quiz quiz, List<Question> questions, List<String> userAnswers) {
        if (timerService != null && !timerService.isShutdown()) {
            timerService.shutdownNow(); // Ensure any old timer is stopped
        }
        timerService = Executors.newSingleThreadScheduledExecutor();
        timerService.scheduleAtFixedRate(() -> {
            javafx.application.Platform.runLater(() -> {
                if (timeLeftSeconds <= 0) {
                    stopTimer();
                    timerLabel.setText("Time's Up!");
                    showAlert(Alert.AlertType.INFORMATION, "Time's Up!", "Your time for the quiz has run out! Submitting your answers.");
                    calculateAndRecordScore(quiz, questions, userAnswers);
                    primaryStage.setScene(createUserMenuScene()); // Go back to user menu
                } else {
                    int minutes = timeLeftSeconds / 60;
                    int seconds = timeLeftSeconds % 60;
                    timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
                    timerProgress.setProgress((double) timeLeftSeconds / (quiz.getTimeLimitMinutes() * 60));
                    timeLeftSeconds--;
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopTimer() {
        if (timerService != null && !timerService.isShutdown()) {
            timerService.shutdownNow();
            System.out.println("Timer service shut down.");
        }
    }

    private void calculateAndRecordScore(Quiz quiz, List<Question> questions, List<String> userAnswers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String userAnswer = userAnswers.get(i);
            if (userAnswer != null && userAnswer.equalsIgnoreCase(q.getCorrectOption())) {
                score++;
            }
        }

        // Record the attempt
        String attemptDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        QuizAttempt newAttempt = new QuizAttempt(currentUser.getId(), quiz.getId(), score, questions.size(), attemptDate);
        if (quizManager.recordQuizAttempt(newAttempt)) {
            showAlert(Alert.AlertType.INFORMATION, "Quiz Complete",
                      String.format("You scored %d out of %d questions!", score, questions.size()));
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record quiz attempt.");
        }
    }

    private Scene createViewMyAttemptsScene() {
    VBox root = new VBox(10);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.TOP_LEFT);
    root.getStyleClass().add("scene-background"); // Apply background

    Label headerLabel = new Label("My Quiz Attempts");
    headerLabel.getStyleClass().add("header-label"); // Apply header style

    ListView<QuizAttempt> attemptListView = new ListView<>();
    attemptListView.getStyleClass().add("list-view"); // Apply list view style
    List<QuizAttempt> userAttempts = quizManager.getUserQuizAttempts(currentUser.getId());
    ObservableList<QuizAttempt> attempts = FXCollections.observableArrayList(userAttempts);
    attemptListView.setItems(attempts);
    System.out.println("Attempts loaded into UI (User): " + attempts.size());

    Button backButton = new Button("Back to User Menu");
    backButton.setOnAction(e -> {
        Scene nextScene = createUserMenuScene();
        applyCss(nextScene);
        primaryStage.setScene(nextScene);
    });

    root.getChildren().addAll(headerLabel, attemptListView, backButton);
    return new Scene(root, 650, 550); // Increased size
}
    private Scene createViewAllAttemptsScene() {
    VBox root = new VBox(10);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.TOP_LEFT);
    root.getStyleClass().add("scene-background"); // Apply background

    Label headerLabel = new Label("All Quiz Attempts (Admin View)");
    headerLabel.getStyleClass().add("header-label"); // Apply header style

    ListView<QuizAttempt> attemptListView = new ListView<>();
    attemptListView.getStyleClass().add("list-view"); // Apply list view style
    List<QuizAttempt> allAttempts = quizManager.getAllQuizAttempts();
    ObservableList<QuizAttempt> attempts = FXCollections.observableArrayList(allAttempts);
    attemptListView.setItems(attempts);
    System.out.println("Attempts loaded into UI (Admin): " + attempts.size());

    Button backButton = new Button("Back to Admin Menu");
    backButton.setOnAction(e -> {
        Scene nextScene = createAdminMenuScene();
        applyCss(nextScene);
        primaryStage.setScene(nextScene);
    });

    root.getChildren().addAll(headerLabel, attemptListView, backButton);
    return new Scene(root, 750, 650); // Increased size
}

    // --- Utility Methods ---

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        // Ensure the timer service is shut down when the application closes
        stopTimer();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void applyCss(Scene scene) {
        // Ensure the path to style.css is correct.
        // It's assumed style.css is in the 'src' directory, so this path works relative to the classpath.
        String cssPath = QuizApplication.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
    }
}
// This