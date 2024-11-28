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
        this.questions = new Question[game.questionsPerRound];
    }

    public void setCategory(String category) throws Exception {
        this.category = category;
        db.loadQuestions(category);
    }

    public String getQuestions() throws Exception {
        StringBuilder temp = new StringBuilder();
        String questionSet = "QuestionSet≡ [";
        for (int i = 0; i < questions.length; i++) {
            temp.append(questions[i].getShuffled());
            if (i < questions.length - 1) {
                temp.append(", |, ");
            }
        }
        questionSet += temp + "]";
        return questionSet;
    }

    public void incrementAnsweredQuestions() {
        answeredQuestions++;
        game.currentQuestion = answeredQuestions;
    }

    public String checkAnswer(String answer) {
        boolean rightAnswer;
        String reply = "Solution: ";
        if (answer.equals(questions[game.currentQuestion].rightAnswer)) {
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

    public void resetCounters() {
        answeredQuestions = 0;
        player1AnsweredQuestions = 0;
        player2AnsweredQuestions = 0;
    }

    public boolean finished() {
        return player1AnsweredQuestions == game.questionsPerRound
               && player2AnsweredQuestions == game.questionsPerRound;
    }
}
