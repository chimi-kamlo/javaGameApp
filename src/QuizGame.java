import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizGame extends JFrame {
    private List<Question> questions = new ArrayList<>();
    private List<Question> currentLevelQuestions = new ArrayList<>();
    private int correctAnswers = 0;
    private int questionsAsked = 0;
    private Question currentQuestion;
    private JLabel questionLabel;
    private List<JCheckBox> answerCheckBoxes = new ArrayList<>();
    private JButton submitButton;
    private JButton changeDifficultyButton;
    private JLabel scoreLabel;
    private int selectedDifficulty = 1;  // Default difficulty level

    public QuizGame() {
        setTitle("Quiz Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showDifficultySelection();
    }

    private void showDifficultySelection() {
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Select difficulty level:",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            selectedDifficulty = 1;
        } else if (choice == 1) {
            selectedDifficulty = 2;
        } else if (choice == 2) {
            selectedDifficulty = 3;
        } else {
            System.exit(0);  // Exit if no option selected
        }

        initializeGame();
    }

    private void initializeGame() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        questionLabel = new JLabel("Question", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(questionLabel, BorderLayout.NORTH);

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(scoreLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel answerPanel = new JPanel();
        answerPanel.setLayout(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            JCheckBox checkBox = new JCheckBox();
            answerCheckBoxes.add(checkBox);
            answerPanel.add(checkBox);
        }
        add(answerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        submitButton = new JButton("Submit Answer");
        submitButton.addActionListener(new SubmitButtonListener());
        bottomPanel.add(submitButton, BorderLayout.CENTER);

        changeDifficultyButton = new JButton("Change Difficulty");
        changeDifficultyButton.addActionListener(new ChangeDifficultyButtonListener());
        bottomPanel.add(changeDifficultyButton, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestions();
        prepareQuestionsForCurrentLevel();
        nextQuestion();
    }

    private void loadQuestions() {
        try (BufferedReader br = new BufferedReader(new FileReader("questions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    String question = parts[0];
                    String correctAnswer = parts[1];
                    List<String> distractors = new ArrayList<>();
                    for (int i = 2; i < 5; i++) {
                        distractors.add(parts[i]);
                    }
                    int difficulty = Integer.parseInt(parts[5]);
                    questions.add(new Question(question, correctAnswer, distractors, difficulty));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareQuestionsForCurrentLevel() {
        currentLevelQuestions.clear();
        for (Question q : questions) {
            if (q.getDifficultyLevel() == selectedDifficulty) {
                currentLevelQuestions.add(q);
            }
        }
        Collections.shuffle(currentLevelQuestions);
        if (currentLevelQuestions.size() > 10) {
            currentLevelQuestions = currentLevelQuestions.subList(0, 10);
        }
    }

    private void nextQuestion() {
        if (questionsAsked >= 10) {
            showCompletionDialog();
            return;
        }

        if (currentLevelQuestions.isEmpty()) {
            showCompletionDialog();
            return;
        }

        currentQuestion = currentLevelQuestions.remove(0);
        questionsAsked++;
        questionLabel.setText(currentQuestion.getQuestion());

        List<String> allAnswers = new ArrayList<>(currentQuestion.getDistractors());
        allAnswers.add(currentQuestion.getCorrectAnswer());
        Collections.shuffle(allAnswers);

        for (int i = 0; i < answerCheckBoxes.size(); i++) {
            answerCheckBoxes.get(i).setText(allAnswers.get(i));
            answerCheckBoxes.get(i).setSelected(false);
        }
    }

    private void showCompletionDialog() {
        String message;
        if (correctAnswers >= 9) {
            message = "Congratulations! You scored " + correctAnswers + "/10. Do you want to proceed to the next level?";
        } else {
            message = "You scored " + correctAnswers + "/10. Try again or select a different level.";
        }

        int option = JOptionPane.showOptionDialog(
                this,
                message,
                "Level Completed",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Yes", "No"},
                "Yes"
        );

        if (option == JOptionPane.YES_OPTION && correctAnswers >= 9) {
            selectedDifficulty++;
            if (selectedDifficulty > 3) {
                JOptionPane.showMessageDialog(this, "Congratulations! You have completed all levels!");
                System.exit(0);
            } else {
                questionsAsked = 0;
                correctAnswers = 0;
                prepareQuestionsForCurrentLevel();
                showDifficultySelection();
            }
        }
    }


    private void showWrongAnswerDialog() {
        int option = JOptionPane.showOptionDialog(
                this,
                "Wrong answer! The correct answer was: " + currentQuestion.getCorrectAnswer() + ". Do you want to retry the question or continue?",
                "Wrong Answer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Retry", "Continue"},
                "Retry"
        );

        if (option == JOptionPane.YES_OPTION) {
            displayCurrentQuestion();
        } else {
            nextQuestion();
        }
    }

    private void displayCurrentQuestion() {
        questionLabel.setText(currentQuestion.getQuestion());

        List<String> allAnswers = new ArrayList<>(currentQuestion.getDistractors());
        allAnswers.add(currentQuestion.getCorrectAnswer());
        Collections.shuffle(allAnswers);

        for (int i = 0; i < answerCheckBoxes.size(); i++) {
            answerCheckBoxes.get(i).setText(allAnswers.get(i));
            answerCheckBoxes.get(i).setSelected(false);
        }
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (JCheckBox checkBox : answerCheckBoxes) {
                if (checkBox.isSelected()) {
                    if (checkBox.getText().equals(currentQuestion.getCorrectAnswer())) {
                        correctAnswers++;
                        scoreLabel.setText("Score: " + correctAnswers);
                        nextQuestion();
                    } else {
                        showWrongAnswerDialog();
                    }
                    break;
                }
            }
        }
    }

    private class ChangeDifficultyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showDifficultySelection();
        }
    }

    private static class Question {
        private final String question;
        private final String correctAnswer;
        private final List<String> distractors;
        private final int difficultyLevel;

        public Question(String question, String correctAnswer, List<String> distractors, int difficultyLevel) {
            this.question = question;
            this.correctAnswer = correctAnswer;
            this.distractors = distractors;
            this.difficultyLevel = difficultyLevel;
        }

        public String getQuestion() {
            return question;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public List<String> getDistractors() {
            return distractors;
        }

        public int getDifficultyLevel() {
            return difficultyLevel;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizGame game = new QuizGame();
            game.setVisible(true);
        });
    }
}

