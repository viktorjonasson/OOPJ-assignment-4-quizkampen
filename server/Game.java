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
    BufferedReader readerPlayer2;
    int[][] player1Res = new int[6][3];
    int[][] player2Res = new int[6][3];
    final int GAME_ID;
    int currentRound = 1;
    int currentPlayer = 1;
    int currentQuestion = 1;
    private final DataBase db;
    boolean gameStarted = false;

    Game(Socket player1Socket, int gameId) throws IOException {
        this.player1 = player1Socket;
        this.db = new DataBase();
        writerPlayer1 = new PrintWriter(player1.getOutputStream(), true);
        GAME_ID = gameId;
        start();
    }

    public void run() {
        String request;
        String reply;
        String[] parts;
        Round round = new Round(db, this);
        while (true) {
            System.out.println("Inside loop");
            if (player1 != null && player2 != null) {
                handleClientResponse();
                System.out.println("2 players connected");
                if (round.answeredQuestions == 3) {
                    switchPlayer();
                    writeToClient(round.getQuestions());
                }
                System.out.println("Both players connected");
            } else {
                if (player1 != null) {
                    handleClientResponse();
//                    if (!gameStarted) {
//                        try {
//                            readerPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
//                            handleCategorySet();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                        gameStarted = true;
//                    } else {
//                        try {
//                            if ((request = readerPlayer1.readLine()) != null && round.answeredQuestions != 3) {
//                                if (request.startsWith("Category")) {
//                                    parts = request.split(":");
//                                    round.setCategory(parts[1]);
//                                    writeToClient(round.getQuestions());
//                                }
//                                if (request.startsWith("Answer") && round.answeredQuestions < 3) {
//                                    parts = request.split(":");
//                                    reply = round.checkAnswer(parts[1].trim());
//                                    writeToClient(reply);
//                                    round.incrementAnsweredQuestions();
//                                }
//                            }
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
                }
            }

        }
    }

    public void handleClientResponse() {
        String request;
        String reply;
        String[] parts;
        Round round = new Round(db, this);
        BufferedReader reader;
        Socket playerSocket;
        if (currentPlayer == 1) {
            reader = readerPlayer1;
            playerSocket = player1;
            if (reader == null) {
                try {
                    reader = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
                    handleCategorySet();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            reader = readerPlayer2;
            playerSocket = player2;
        }
        if (!gameStarted) {
            gameStarted = true;
        } else {
            try {
                if ((request = reader.readLine()) != null && round.answeredQuestions != 3) {
                    if (request.startsWith("Category")) {
                        parts = request.split(":");
                        round.setCategory(parts[1]);
                        writeToClient(round.getQuestions());
                    }
                    if (request.startsWith("Answer") && round.answeredQuestions < 3) {
                        parts = request.split(":");
                        reply = round.checkAnswer(parts[1].trim());
                        writeToClient(reply);
                        round.incrementAnsweredQuestions();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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


}
