import java.util.ArrayList;

public class Round {
    String category;
    Question[] questions;
    int answeredQuestions = 0;
    DataBase db;
    Game game;

    public Round(DataBase db, Game game) {
        this.db = db;
        this.game = game;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestions() {
        ArrayList<String> questionSet = db.getQuestionSet();
        return "QuestionSet: " + questionSet.toString();
    }

    public void incrementAnsweredQuestions() {
        answeredQuestions++;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public String checkAnswer(String answer) {
        String reply = "Solution: ";
        if (answer.equals("Right answer")) {
            reply += answer + ", true";
        } else {
            reply += answer + ", false";
        }
        //TODO: funktion för att uppdatera poäng i rätt game-instans
        return reply;
    }
}
