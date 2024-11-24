import java.net.Socket;

public class Game {
    Socket player1;
    Socket player2;
    int[][] player1Res = new int[6][3];
    int[][] player2Res = new int[6][3];
    final int GAME_ID; //Antar att final är rätt iom att ett rum aldrig byter id?
    int currentRound = 1;
    int currentPlayer = 1;
    int currentQuestions = 1;

    void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    Game(Socket player1Socket, int gameId) {
        this.player1 = player1Socket;
        GAME_ID = gameId;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    //Vet inte om vi ska ha dessa två metoder, lägger in dem tillfälligt.
    public void newRound() {
        currentRound++;
    }

    //Nu har vi råkat lägga denna metod på två ställen (vi kanske haft två tickets)?
    //Ska den vara här eller i gameHandler? /Ylva
    public static boolean checkAnswer(String clientAnswer, String correctAnswer) {
        return clientAnswer.equals(correctAnswer);
    }
}
