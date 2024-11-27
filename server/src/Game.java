import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Properties;

public class Game extends Thread {
    Properties gameProperties = new Properties();
    Socket player1;
    Socket player2;
    PrintWriter writerPlayer1;
    PrintWriter writerPlayer2;
    BufferedReader readerPlayer1;
    BufferedReader readerPlayer2;
    int gameRounds;
    int questionsPerRound;
    int[][] player1Res;
    int[][] player2Res;
    final int GAME_ID;
    int currentPlayer = 1;
    int currentRound = 0;
    int currentQuestion = 0;
    boolean gameStarted = false;
    boolean player1Initiated = false;
    boolean player2Initiated = false;
    private final DataBase db;

    Game(Socket player1Socket, int gameId) throws IOException {
        readGameProperties();
        player1Res = new int[gameRounds][questionsPerRound];
        player2Res = new int[gameRounds][questionsPerRound];
        this.player1 = player1Socket;
        this.db = new DataBase(questionsPerRound);
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
                    if (round.answeredQuestions != questionsPerRound) {
                        handleClientRequest(round);
                    }
                    if (round.answeredQuestions == questionsPerRound && !round.finished()) {
                        sendResults();
                        switchPlayer();
                        writeToClient(round.getQuestions());
                        round.answeredQuestions = 0;
                    }
                    if (round.finished()) {
                        switchPlayer();
                        sendResults();
                        switchPlayer();
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
                if (player1 != null && round.answeredQuestions != questionsPerRound) {
                    if (!gameStarted) {
                        handleNewGame();
                    } else {
                        handleClientRequest(round);
                    }
                }
            }
        }
    }

    private void readGameProperties() {
        try (FileInputStream input = new FileInputStream("server/src/game-config.properties")) {
            gameProperties.load(input);
            gameRounds = Integer.parseInt(gameProperties.getProperty("amountOfRounds"));
            questionsPerRound = Integer.parseInt(gameProperties.getProperty("amountOfQuestionsPerRound"));
        } catch (IOException e) {
            e.printStackTrace();
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
            sendGameProperties(1);
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
                if (request.startsWith("PropertiesReceived")) {
                    if (!player1Initiated) {
                        writerPlayer1.println("CategorySet: " + Arrays.toString(db.getCategorySet()));
                        player1Initiated = true;
                    }
                }
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

    public void setPlayerResult(boolean correctAnswer) {
        if (correctAnswer) {
            if (currentPlayer == 1) {
                player1Res[currentRound][currentQuestion] = 1;
            } else {
                player2Res[currentRound][currentQuestion] = 1;
            }
        } else {
            if (currentPlayer == 1) {
                player1Res[currentRound][currentQuestion] = -1;
            } else {
                player2Res[currentRound][currentQuestion] = -1;
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

    public void sendGameProperties(int player) {
        String reply = "GameProperties: " + gameRounds + ", " + questionsPerRound;
        if (player == 1) {
            writerPlayer1.println(reply + "|Player1");
        } else {
            writerPlayer2.println(reply + "|Player2");
        }
    }

    public void sendResults() {
        String player1Result = "Player1Res: " + Arrays.deepToString(player1Res);
        String player2Result = "Player2Res: " + Arrays.deepToString(player2Res);
        writeToClient("PlayerResults: " + player1Result + player2Result);
    }
}