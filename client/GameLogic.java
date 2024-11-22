import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class GameLogic {
    ArrayList<String[]> questions;

    GameLogic() {
        questions = new ArrayList<>();
    }

    public void loadQuestionSet(String serverResponse) {
        System.out.println(serverResponse);
        String[] categorySet = serverResponse.substring(1, serverResponse.length() - 1).split("\\|");

        for (String category : categorySet) {
            String[] questionSet = category.trim().replaceAll("^,\\s*|\\s*,$", "").split(",\\s*");
            System.out.println(Arrays.toString(questionSet));
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
