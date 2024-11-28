import java.util.ArrayList;
import java.util.Collections;

public class Question {
    private final String category;
    String question;
    ArrayList<String> wrongAnswers;
    String rightAnswer;

    public Question(
            String category, String question, ArrayList<String> wrongAnswer, String rightAnswer) {
        this.category = category;
        this.question = question;
        this.wrongAnswers = wrongAnswer;
        this.rightAnswer = rightAnswer;
    }

    public String getQuestion() {
        return category;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String getShuffled() {
        ArrayList<String> shuffledQuestions = new ArrayList<>();
        for (String wrong : wrongAnswers) {
            shuffledQuestions.add(wrong + "≈");
        }
        shuffledQuestions.add(rightAnswer + "≈");
        Collections.shuffle(shuffledQuestions);
        shuffledQuestions.addFirst(question + "≈");
        String output = shuffledQuestions.toString();
        output = output.substring(1, output.length() - 1);
        return output;
    }
}
