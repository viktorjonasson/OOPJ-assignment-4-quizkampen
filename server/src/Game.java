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
    int amountOfGameRounds;
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
    boolean activeGame = true;
    private final DataBase db;
    Round round;

    Game(Socket player1Socket, int gameId) throws IOException {
        readGameProperties();
        player1Res = new int[amountOfGameRounds][questionsPerRound];
        player2Res = new int[amountOfGameRounds][questionsPerRound];
        this.player1 = player1Socket;
        this.db = new DataBase(questionsPerRound, this);
        writerPlayer1 = new PrintWriter(player1.getOutputStream(), true);
        GAME_ID = gameId;
        this.round = new Round(db, this);
        start();
    }

    public void run() {
        while (activeGame) {
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
                        sendResults(); //To P1
                        switchPlayer();
                        try {
                            writeToClient(round.getQuestions());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        round.answeredQuestions = 0;
                    }
                    if (round.finished()) {
                        switchPlayer();
                        sendResults();
                        switchPlayer();
                        sendResults();
                        sendCategoriesToClient();
                        currentRound++;
                        currentQuestion = 0;
                        round.resetCounters();
                    }
                    if (currentRound == amountOfGameRounds) {
                        break;
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
        if (writerPlayer1 != null) {
            writerPlayer1.println("GameEnded");
        }
        if (writerPlayer2 != null) {
            writerPlayer2.println("GameEnded");
        }
        endGame();
    }

    private void shutdown() {
        activeGame = false;
    }

    private void readGameProperties() {
        try (FileInputStream input = new FileInputStream("server/src/game-config.properties")) {
            gameProperties.load(input);
            amountOfGameRounds = Integer.parseInt(gameProperties.getProperty("amountOfRounds"));
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
            if (currentRound.answeredQuestions != questionsPerRound &&
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
                    String cleanedString = addPrefixToCategory(parts[1].trim());
                    currentRound.setCategory(cleanedString);
                    //
                    writeToClient(currentRound.getQuestions());
                }
                if (request.startsWith("NewGame")) {
                    sendGameProperties(1);
                }
                if (request.startsWith("Answer") && currentRound.answeredQuestions < questionsPerRound) {
                    parts = request.split(":");
                    reply = currentRound.checkAnswer(parts[1].trim());
                    writeToClient(reply);
                    currentRound.incrementAnsweredQuestions();
                }
                if (request.startsWith("ClientClosing")) {
                    shutdown();
                }

            }
        } catch (Exception e) {
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
        String reply = "GameProperties: " + amountOfGameRounds + ", " + questionsPerRound;
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

    public String addPrefixToCategory (String inputCategory) {
        String[] entertainmentCategories = {
                "Books", "Film", "Music", "Musicals & Theatres", "Television",
                "Video Games", "Board Games", "Japanese Anime & Manga", "Cartoon & Animations", "Comics"
        };

        String[] scienceCategories = {
                "Computers", "Mathematics"
        };

        boolean matched = false;

        for (String category : entertainmentCategories) {
            if (inputCategory.equalsIgnoreCase(category)) {
                inputCategory = "Entertainment: " + category;
                matched = true;
                break;
            }
        }

        if (!matched) {
            for (String category : scienceCategories) {
                if (inputCategory.equalsIgnoreCase(category)) {
                    inputCategory = "Science: " + category;
                    break;
                }
            }
        }
        return inputCategory;
    }

    public void endGame() {
        try {
            if (player1 != null) {
                player1.close();
            }
            if (player2 != null) {
                player2.close();
            }
            if (writerPlayer1 != null) {
                writerPlayer1.close();
            }
            if (writerPlayer2 != null) {
                writerPlayer2.close();
            }
            if (readerPlayer1 != null) {
                readerPlayer1.close();
            }
            if (readerPlayer2 != null) {
                readerPlayer2.close();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}