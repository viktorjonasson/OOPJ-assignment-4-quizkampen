import java.util.ArrayList;

public class Round {
    DataBase db;
    Game game;
    String category;
    Question[] questions;
    int answeredQuestions = 0;
    int player1AnsweredQuestions = 0;
    int player2AnsweredQuestions = 0;

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
        game.currentQuestion = answeredQuestions;
    }

    public String checkAnswer(String answer) {
        boolean rightAnswer;
        String reply = "Solution: ";
        if (answer.equals("Right answer")) {
            reply += answer + ", true";
            rightAnswer = true;
        } else {
            reply += answer + ", false";
            rightAnswer = false;
        }
        if (game.currentPlayer == 1) {
            player1AnsweredQuestions++;
        } else {
            player2AnsweredQuestions++;
        }
        game.setPlayerResult(rightAnswer);
        return reply;
    }

    public boolean finished() {
        return player1AnsweredQuestions == game.questionsPerRound
               && player2AnsweredQuestions == game.questionsPerRound;
    }
}
