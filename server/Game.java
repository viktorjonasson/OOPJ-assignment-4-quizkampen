import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Game extends Thread {
    Socket player1;
    Socket player2;
    PrintWriter writerPlayer1;
    PrintWriter writerPlayer2;
    BufferedReader readerPlayer1;
    int[][] player1Res = new int[6][3];
    int[][] player2Res = new int[6][3];
    final int GAME_ID;
    int currentRound = 1;
    int currentPlayer = 1;
    int currentQuestion = 1;
    private DataBase db;
    boolean gameStarted = false;

    Game(Socket player1Socket, int gameId) throws IOException {
        this.player1 = player1Socket;
        this.db = new DataBase();
        writerPlayer1 = new PrintWriter(player1.getOutputStream(), true);
        GAME_ID = gameId;
        start();
    }

    public void run() {
        System.out.println("Utanför");
        String request;
        String[] parts;
        while (true) {
            if (player1 != null && player2 != null) {
                System.out.println("Both players connected");
            } else {
                if (player1 != null) {
                    if (!gameStarted) {
                        try {
                            readerPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
                            handleCategorySet();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        gameStarted = true;
                    } else {
                        try {
                            if ((request = readerPlayer1.readLine()) != null) {
                                if (request.startsWith("Category")) {
                                    parts = request.split(":");
                                    handleQuestionSet();
                                }
                                if (request.startsWith("Answer")) {
                                    parts = request.split(":");
                                    checkAnswer(parts[1].trim());
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public void updateResult(boolean correctAnswer) {
        if (currentPlayer == 1) {
            setPlayer1Res(correctAnswer);
        } else
            setPlayer2Res(correctAnswer);

        switchPlayer();

        //Detta anrop funkar inte för två spelare.
        updateCounters();
    }

    public void updateCounters() {
        ++currentQuestion;
        if (currentQuestion < 3) {
            currentQuestion = 1;
            ++currentRound;
        }
    }

    public void setPlayer1Res(boolean correctAnswer) {
        if (correctAnswer) {
            player1Res[currentRound - 1][currentQuestion - 1] = 1; //rätt svar
        } else
            player1Res[currentRound - 1][currentQuestion - 1] = -1; //fel svar
    }

    public void setPlayer2Res(boolean correctAnswer) {
        if (correctAnswer) {
            player2Res[currentRound - 1][currentQuestion - 1] = 1; //rätt svar
        } else
            player2Res[currentRound - 1][currentQuestion - 1] = -1; //fel svar
    }

    void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    protected void writeToClient(String data) {
        if (currentPlayer == 1) {
            writerPlayer1.println(data);
        } else {
            writerPlayer2.println(data);
        }
    }

    public void handleCategorySet() {
        String reply = "CategorySet: " + Arrays.toString(db.getCategorySet());
        writeToClient(reply);
    }

    public void handleQuestionSet() {
        ArrayList<String> questionSet = db.getQuestionSet();
        String reply = "QuestionSet: " + questionSet.toString();
        writeToClient(reply);
    }

    void checkAnswer(String answer) {
        String reply = "Solution: ";
        if (answer.equals("Option 1")) {
            reply += answer + ", true";
        } else {
            reply += answer + ", false";
        }
        //TODO: funktion för att uppdatera poäng i rätt game-instans
         writeToClient(reply);
    }
}
