import java.util.ArrayList;
import java.util.Collections;

public class Question {
    String category;
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
        return question;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public ArrayList<String> getShuffled() {
        ArrayList<String> shuffledQuestions = wrongAnswers;
        shuffledQuestions.add(rightAnswer);
        Collections.shuffle(shuffledQuestions);
        shuffledQuestions.addFirst(category);

        return shuffledQuestions;
    }
}
