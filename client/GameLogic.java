import java.util.ArrayList;
import java.util.Optional;

public class GameLogic {
    ArrayList<String[]> questions;

    GameLogic() {
        questions = new ArrayList<>();
    }

    public void loadQuestionSet(String serverResponse) {
        System.out.println(serverResponse);
        String[] questionSet = serverResponse.substring(1, serverResponse.length() - 1).split(",\\s*");
        questions.add(questionSet);
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
