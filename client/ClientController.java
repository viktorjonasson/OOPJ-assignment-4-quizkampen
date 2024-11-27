import javax.swing.*;
import java.util.Arrays;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Optional;

import static java.util.Collections.replaceAll;

public class ClientController {
    Client client;
    GUI gui;
    GameLogic gameLogic;
    GameState gameState;
    boolean isCategoryChooser;

    ClientController() {
        gui = new GUI();
        int port = 12345;
        String address = "127.0.0.1";
        client = new Client(address, port, this);
        gui.gameBoard();
        gameLogic = new GameLogic();
        initializeButtonListeners(gui.getOptionButtons());
        initializeContinueButtonListener(gui.continueBtn);
        initializeCategoryButtons(gui.getCategoryButtons());
        initializeStartRoundCategoryBtn(gui.startRoundCategoryBtn);
        initializeStartRoundQuestionBtn(gui.startRoundQuestionBtn);
        startNewGame();
    }

    void initializeContinueButtonListener(JButton continueBtn) {
        continueBtn.addActionListener(_ -> {
            Optional<String[]> question = gameLogic.getNextQuestion();
            if (question.isPresent()) {
                gui.updateQuestionPanel(question.get());
                gui.lockContinueButton();
            }else
                gui.switchPanel(GameState.SCORE_TABLE);
        });
    }

    void initializeStartRoundCategoryBtn(JButton startRoundCategoryBtn) {
        startRoundCategoryBtn.addActionListener(_ -> {
            gui.switchPanel(GameState.CHOOSE_CATEGORY);
            gui.lockScoreButton(startRoundCategoryBtn);
            gui.waiting.setVisible(true);
        });
    }

    void initializeStartRoundQuestionBtn(JButton startRoundQuestionBtn) {
        startRoundQuestionBtn.addActionListener(_ -> {
            gui.switchPanel(GameState.ANSWER_QUESTION);
        });
    }

    void scoreButtonCategoryMode(){
            gui.unLockScoreButton(gui.startRoundCategoryBtn);
            gui.lockScoreButton(gui.startRoundQuestionBtn);
            isCategoryChooser = true;
    }

    void scoreButtonQuestionMode(){
        if (isCategoryChooser){
            gui.lockScoreButton(gui.startRoundCategoryBtn);
            gui.waiting.setVisible(true);
            isCategoryChooser = false;
        }else{
            gui.unLockScoreButton(gui.startRoundQuestionBtn);
            gui.waiting.setVisible(false);
        }
    }

    void initializeCategoryButtons(JButton[] categoryButtons) {
        for (JButton button : categoryButtons) {
            button.addActionListener(_ -> {
                String category = "Category: " + button.getText();
                client.writeToServer(category);
                gui.lockButtons(categoryButtons);
            });
        }
    }

    void initializeButtonListeners(JButton[] answerButtons) {
        for (JButton button : answerButtons) {
            button.addActionListener(_ -> {
                String answer = "Answer: " + button.getText();
                client.writeToServer(answer);
                gui.lockButtons(answerButtons);
                gui.unLockContinueButton();
            });
        }
    }

    void handleQuestionSet(String notification) {
        gameLogic.loadQuestionSet(notification);
        Optional<String[]> question = gameLogic.getNextQuestion();
        if (question.isPresent()) {
            gui.lockContinueButton();
            gui.updateQuestionPanel(question.get());
            if (isCategoryChooser){
                gui.switchPanel(GameState.ANSWER_QUESTION);
            }else{
                gui.switchPanel(GameState.SCORE_TABLE);
            }
        } else {
            System.err.println("Error loading question.");
        }
    }

    void handleCategorySet(String notification) {
        String[] categoryChoice = notification.substring(1, notification.length() - 1).split("\\|");
        gui.updateCategoryPanel(categoryChoice);
        isCategoryChooser = true;
    }

    void startNewGame() {
        gui.switchPanel(GameState.CHOOSE_CATEGORY);
        String request = "NewGame";
        client.writeToServer(request);
    }

    public void handleSolution(String solution) {
        boolean correctAnswer = false;
        String[] parts = solution.split(",");
        if (parts[1].trim().equalsIgnoreCase("true")) {
            correctAnswer = true;
        }
        Optional<JButton> pressedButton = gui.getButton(parts[0]);
        if (pressedButton.isPresent()) {
            gui.changeColor(pressedButton.get(), correctAnswer);
        }
    }

    public void handleProperties(String serverReply) {
        int gameRounds, questionsPerRound;
        String[] parts;
        parts = serverReply.split(",");
        gameRounds = Integer.parseInt(parts[0].trim());
        questionsPerRound = Integer.parseInt(parts[1].trim());
        gui.loadProperties(gameRounds, questionsPerRound);
    }

    public void handlePlayerResults(String serverReply) {
        String[] parts = serverReply.split("Player2Res:");

        // Remove prefixes and split into rows
        String player1ResultsStr = parts[0]
                .replace("Player1Res: ", "")
                .replace("[[", "")
                .replace("]]", "")
                .replace("], [", ", ")
                .replaceAll("\\s+", "");
        String player2ResultsStr = parts[1]
                .replace("[[", "")
                .replace("]]", "")
                .replace("], [", ", ")
                .replaceAll("\\s+", "");

        // Split and convert to int array
        int[] player1Results = Arrays.stream(player1ResultsStr.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        int[] player2Results = Arrays.stream(player2ResultsStr.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        gui.changeScoreColor(player1Results, player2Results);
    }

    public static void main(String[] args) {
        new ClientController();
    }
}
