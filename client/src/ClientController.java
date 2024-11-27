import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;

public class ClientController {
    Client client;
    GUI gui;
    GameLogic gameLogic;
    boolean isCategoryChooser;

    ClientController(GUI gui, GameLogic gameLogic, String IP, int port) {
        this.gui = gui;
        this.gameLogic = gameLogic;
        this.client = new Client(IP, port,this);
        gui.gameBoard();
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
        startRoundQuestionBtn.addActionListener(_ -> gui.switchPanel(GameState.ANSWER_QUESTION));
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
        gameLogic.updateScorePanel(correctAnswer);
        Optional<JButton> pressedButton = gui.getButton(parts[0]);
        if (pressedButton.isPresent()) {
            gui.changeColor(pressedButton.get(), correctAnswer);
        }
    }

    public void handleProperties(String serverReply) {
        String[] initialPart = serverReply.split("\\|");
        int gameRounds, questionsPerRound;
        String[] propertiesPart = initialPart[0].split(",");
        String playerString = initialPart[1].replace("Player", "");
        gameLogic.setPlayer(Integer.parseInt(playerString));
        gameRounds = Integer.parseInt(propertiesPart[0].trim());
        questionsPerRound = Integer.parseInt(propertiesPart[1].trim());
        gui.loadProperties(gameRounds, questionsPerRound);
    }

    public void handlePlayerResults(String serverReply) {
        String[] parts = serverReply.split("Player2Res:");

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

        int[] player1Results = Arrays.stream(player1ResultsStr.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        int[] player2Results = Arrays.stream(player2ResultsStr.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        gui.changeScoreColor(player1Results, player2Results);
    }
}