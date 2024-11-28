import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameLogic {
    private ArrayList<String[]> questions;
    private final GUI gui;
    private int currentQuestion = 0;
    private int player = 0;

    GameLogic(GUI gui) {
        this.gui = gui;
        questions = new ArrayList<>();
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void updateScorePanel(boolean correctAnswer) {
        gui.updateLocalResultLabel(currentQuestion, correctAnswer, player);
        currentQuestion++;
    }

    public void loadQuestionSet(String serverResponse) {
        String[] categorySet = serverResponse.substring(1, serverResponse.length() - 1).split("\\|");

        for (String category : categorySet) {
            // Split by "≈" and remove any extraneous spaces or commas
            String[] questionSet = category.trim().replaceAll("^≈\\s*|\\s*≈$", "").split("≈");

            // Trim each question and remove any leading commas and spaces
            List<String> cleanedQuestions = new ArrayList<>();
            for (String question : questionSet) {
                String cleaned = question.trim().replaceAll("^,\\s*", "");  // Remove leading commas and spaces
                if (!cleaned.isEmpty()) {  // Skip empty strings
                    cleanedQuestions.add(cleaned);
                }
            }

            // Add the cleaned question set to the main list
            questions.add(cleanedQuestions.toArray(new String[0]));  // Assuming 'questions' is a List of String arrays
        }
    }

    public Optional<String[]> getNextQuestion() {
        Optional<String[]> question;
        if (!questions.isEmpty()) {
            question = Optional.of(questions.getFirst());
            questions.removeFirst();
            return question;
        } else {
            return Optional.empty();
        }
    }
}