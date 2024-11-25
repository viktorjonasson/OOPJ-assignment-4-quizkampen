import java.net.Socket;

public class Game {
    Socket player1;
    Socket player2;
    int[][] player1Res = new int[6][3];
    int[][] player2Res = new int[6][3];
    final int GAME_ID;
    int currentRound = 1;
    int currentPlayer = 1;
    int currentQuestion = 1;

    Game(Socket player1Socket, int gameId) {
        this.player1 = player1Socket;
        GAME_ID = gameId;
    }

    public void updateResult(boolean correctAnswer) {
        if (currentPlayer == 1){
            setPlayer1Res(correctAnswer);
        }else
            setPlayer2Res(correctAnswer);

        switchPlayer();

        //Detta anrop funkar inte för två spelare.
        updateCounters();
    }

    public void updateCounters(){
        ++currentQuestion;
            if (currentQuestion <3){
                currentQuestion = 1;
                ++currentRound;
            }
    }

    public void setPlayer1Res(boolean correctAnswer){
        if (correctAnswer) {
            player1Res[currentRound-1][currentQuestion -1] = 1; //rätt svar
        }else
            player1Res[currentRound-1][currentQuestion -1] = -1; //fel svar
    }

    public void setPlayer2Res(boolean correctAnswer){
        if (correctAnswer) {
            player2Res[currentRound-1][currentQuestion -1] = 1; //rätt svar
        }else
            player2Res[currentRound-1][currentQuestion -1] = -1; //fel svar
    }

    void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    public int getCurrentQuestion() {return currentQuestion;}

    public int getCurrentRound() {
        return currentRound;
    }
}
