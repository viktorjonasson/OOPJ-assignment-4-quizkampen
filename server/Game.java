import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
    int currentQuestion = 0;
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
        Round round = new Round(db, this);
        while (true) {
            if (player1 != null && player2 != null) {
                if (!gameStarted) {
                    handleNewGame();
                } else {
                    if (readerPlayer2 == null) {
                        initializePlayer2Reader();
                    }
                    if (round.answeredQuestions != 3) {
                        handleClientRequest(round);
                    }
                    if (round.answeredQuestions == 3 && !round.finished()) {
                        switchPlayer();
                        writeToClient(round.getQuestions());
                        round.answeredQuestions = 0;
                    }
                    if (round.finished()) {
                        sendCategoriesToClient();
                        currentRound++;
                        //Följande är en temporär lösning. Vi behöver bygga ut logiken
                        currentQuestion = 0;
                        round.answeredQuestions = 0;
                        round.player1AnsweredQuestions = 0;
                        round.player2AnsweredQuestions = 0;
                    }
                }
            } else {
                if (player1 != null && round.answeredQuestions != 3) {
                    if (!gameStarted) {
                        handleNewGame();
                    } else {
                        handleClientRequest(round);
                    }
                }
            }
        }
    }

    private void initializePlayer2Reader() {
        try {
            readerPlayer2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleNewGame() {
        try {
            readerPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            sendCategoriesToClient();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameStarted = true;
    }

    public void handleClientRequest(Round currentRound) {
        String request;
        String reply;
        String[] parts;
        try {
            if (currentRound.answeredQuestions != 3 &&
                (currentPlayer == 1 && (request = readerPlayer1.readLine()) != null) ||
                (currentPlayer == 2 && (request = readerPlayer2.readLine()) != null)) {
                if (request.startsWith("Category")) {
                    parts = request.split(":");
                    currentRound.setCategory(parts[1]);
                    writeToClient(currentRound.getQuestions());
                }
                if (request.startsWith("Answer") && currentRound.answeredQuestions < 3) {
                    parts = request.split(":");
                    reply = currentRound.checkAnswer(parts[1].trim());
                    writeToClient(reply);
                    currentRound.incrementAnsweredQuestions();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCounters() {
        ++currentQuestion;
        if (currentQuestion < 3) {
            currentQuestion = 1;
            ++currentRound;
        }
    }

    public void setPlayerResult(boolean correctAnswer) {
        if (correctAnswer) {
            if (currentPlayer == 1) {
                player1Res[currentRound - 1][currentQuestion] = 1;
            } else {
                player2Res[currentRound - 1][currentQuestion] = 1;
            }
        } else {
            if (currentPlayer == 1) {
                player1Res[currentRound - 1][currentQuestion] = -1;
            } else {
                player2Res[currentRound - 1][currentQuestion] = -1;
            }
        }
        //Detta är bara för demonstration av score keeping. Ta bort när alla vet hur det funkar
        if (currentPlayer == 1) {
            System.out.println();
            System.out.println("Player 1 score:");
            for (int[] row : player1Res) {
                for (int res : row) {
                    if (res == -1) {
                        System.out.print("W ");
                    }
                    if (res == 1) {
                        System.out.print("R ");
                    }
                    if (res == 0) {
                        System.out.print("N/A ");
                    }
                }
                System.out.println();
            }
        } else {
            System.out.println();
            System.out.println("Player 2 score:");
            for (int[] row : player2Res) {
                for (int res : row) {
                    if (res == -1) {
                        System.out.print("W ");
                    }
                    if (res == 1) {
                        System.out.print("R ");
                    }
                    if (res == 0) {
                        System.out.print("N/A ");
                    }
                }
                System.out.println();
            }
        }
    }

    void switchPlayer() {
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
        currentQuestion = 0;
    }

    protected void writeToClient(String data) {
        if (currentPlayer == 1) {
            writerPlayer1.println(data);
        } else {
            writerPlayer2.println(data);
        }
    }

    public void sendCategoriesToClient() {
        String reply = "CategorySet: " + Arrays.toString(db.getCategorySet());
        writeToClient(reply);
    }
}
