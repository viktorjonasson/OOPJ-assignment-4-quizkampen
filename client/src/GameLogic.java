import java.util.ArrayList;
import java.util.Optional;

public class GameLogic {
    ArrayList<String[]> questions;
    private GUI gui;
    int currentQuestion = 0;
    int player = 0;

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
            String[] questionSet = category.trim().replaceAll("^,\\s*|\\s*,$", "").split(",\\s*");
            questions.add(questionSet);
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
