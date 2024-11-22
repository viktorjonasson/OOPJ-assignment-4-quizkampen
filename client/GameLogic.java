import java.util.ArrayList;
import java.util.Optional;

public class GameLogic {
    ArrayList<String[]> questions;

    GameLogic() {
        questions = new ArrayList<>();
    }

    public void loadQuestionSet(String questions) {
        String[] questionSet = questions.substring(1, questions.length() - 1).split(",\\s*");
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
