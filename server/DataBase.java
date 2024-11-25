import resources.Question;

import java.util.ArrayList;
import java.util.Arrays;

public class DataBase {

    public ArrayList<String> getQuestionSet() {
        String category = "Category";
        Question question_1 = generateQuestion(category);
        Question question_2 = generateQuestion(category);
        Question question_3 = generateQuestion(category);
        ArrayList<String> question1Shuffled = question_1.getShuffled();
        question1Shuffled.add("|");
        ArrayList<String> question2Shuffled = question_2.getShuffled();
        question2Shuffled.add("|");
        ArrayList<String> question3Shuffled = question_3.getShuffled();

        return new ArrayList<>() {{
            addAll(question1Shuffled);
            addAll(question2Shuffled);
            addAll(question3Shuffled);
        }};
    }
    public String[] getCategorySet() {
        return new String[]{"Category 1 |",
                "Category 2 |",
                "Category 3"};
    }

    public Question generateQuestion(String category) {
        String wrongAnswer_1 = "Option 1";
        String wrongAnswer_2 = "Option 2";
        String wrongAnswer_3 = "Option 3";
        String rightAnswer = "Right answer";
        ArrayList<String> wrongAnswers = new ArrayList<>(
                Arrays.asList(wrongAnswer_1, wrongAnswer_2, wrongAnswer_3));
        return new Question(category, "Question 1", wrongAnswers, rightAnswer);
    }
}
