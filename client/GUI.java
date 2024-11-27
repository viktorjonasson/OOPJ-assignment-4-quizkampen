import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;

public class GUI extends JFrame {

    //GUI Setup
    Border outerPadding = BorderFactory.createEmptyBorder(20, 40, 30, 40);
    Border optionButtonsPadding = BorderFactory.createEmptyBorder(20, 0, 20, 0);
    //NYTT NU: Vi använder CardLayout för att panel.
    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    //GUI QuestionPanelStuff
    JPanel questionPanel = new JPanel(new BorderLayout(0, 40));
    JPanel quizContentPanel = new JPanel(new BorderLayout());
    JPanel quizOptionPanel = new JPanel(new GridLayout(2, 2));

    JLabel header = new JLabel("This kategori – Fråga N", SwingConstants.CENTER);
    JLabel question = new JLabel();
    JButton[] optionButtons = new JButton[4];
    JButton continueBtn = new JButton("Fortsätt");

    //CategoryPanelStuff
    JPanel categoryPanel = new JPanel(new BorderLayout(0, 40));
    JPanel catContentPanel = new JPanel(new BorderLayout());
    JButton[] categoryButtons = new JButton[3];
    JPanel catOptionPanel = new JPanel(new GridLayout(3, 1));
    JLabel choseCategory = new JLabel();

    //ScorePanelStuff
    JPanel scorePanel = new JPanel(new BorderLayout());
    JPanel headerPanel = new JPanel(new BorderLayout());
    JLabel gameStatus = new JLabel("Deras tur", SwingConstants.CENTER); //Placeholder x4, sätt dynamiskt sen.
    JLabel player1Name = new JLabel("Spelare 1", SwingConstants.CENTER);
    JLabel player2Name = new JLabel("Spelare 2", SwingConstants.CENTER);
    JLabel totalResult = new JLabel("0 – 0", SwingConstants.CENTER);
    JLabel waiting = new JLabel("WAITING FOR OPPONENT TO FINISH ROUND", SwingConstants.CENTER);
    JPanel player1ResultsPanel = new JPanel(new GridLayout(6, 3));
    JPanel scoreButtonPanel = new JPanel(new BorderLayout());
    JLabel[] player1Results = new JLabel[18];
    JPanel roundIndicatorPanel = new JPanel(new GridLayout(6, 1));
    JLabel[] roundIndicators = new JLabel[6];
    JPanel player2ResultsPanel = new JPanel(new GridLayout(6, 3));
    JLabel[] player2Results = new JLabel[18];
    JButton startRoundCategoryBtn = new JButton("READY FOR CATEGORY CHOICE"); //Denna ska döljas när det är andra spelarens tur.
    JButton startRoundQuestionBtn = new JButton("READY TO ANSWER QUESTIONS");


    //NewGamePanelStuff
    JPanel newGamePanel = new JPanel(new BorderLayout());
    JButton joinGameBtn = new JButton("JOIN GAME");

    //Getter for buttons
    public Optional<JButton> getButton(String buttonText) {
        for (JButton button : optionButtons) {
            if (button.getText().equals(buttonText)) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }

    public JButton[] getOptionButtons() {
        return optionButtons;
    }

    public JButton[] getCategoryButtons() {
        return categoryButtons;
    }

    public void gameBoard() {
        //GUI – Set content
        this.add(mainPanel);
        setupCategoryPanel();
        setupQuestionPanel();
        setupScorePanel();
        mainPanel.add(questionPanel, "Question");
        mainPanel.add(categoryPanel, "Category");
        mainPanel.add(scorePanel, "Score");
        mainPanel.add(newGamePanel, "New Game");

        //Ska förhoppningsvis kunna tas bort. Annars sätt annat default-värde.
        //question.setText("<HTML>Hur många YH-poäng är Nackademins utbildning i Javaprogrammering?</HTML>");

        //GUI – Display it
        setSize(500, 700);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupScorePanel() {
        //ScorePanel – Placement
//        mainPanel.add(scorePanel);
        scorePanel.setVisible(true);
        scorePanel.setBorder(outerPadding);
        scorePanel.add(headerPanel, BorderLayout.NORTH);
        headerPanel.add(gameStatus, BorderLayout.NORTH, SwingConstants.CENTER); //Centrering sätts även här, men behöver ligga där vi sätter innehållet senare, tror jag.
        headerPanel.add(player1Name, BorderLayout.WEST, SwingConstants.CENTER);
        headerPanel.add(totalResult, BorderLayout.CENTER, SwingConstants.CENTER);
        headerPanel.add(player2Name, BorderLayout.EAST, SwingConstants.CENTER);
        scorePanel.add(player1ResultsPanel, BorderLayout.WEST);
        scorePanel.add(roundIndicatorPanel, BorderLayout.CENTER);
        scorePanel.add(player2ResultsPanel, BorderLayout.EAST);
        for (int i = 0; i < roundIndicators.length; i++) {
            roundIndicatorPanel.add(roundIndicators[i] = new JLabel());
            //roundIndicators[i].setPreferredSize(new Dimension(20, 5));
            roundIndicators[i].setText("Round " + (i + 1));
            roundIndicators[i].setHorizontalAlignment(SwingConstants.CENTER);
        }

        waiting.setVisible(false);
        scoreButtonPanel.add(waiting, BorderLayout.CENTER);
        scoreButtonPanel.add(startRoundCategoryBtn, BorderLayout.NORTH);
        scoreButtonPanel.add(startRoundQuestionBtn, BorderLayout.SOUTH);
        scorePanel.add(scoreButtonPanel, BorderLayout.SOUTH);
        lockScoreButton(startRoundCategoryBtn);
        lockScoreButton(startRoundQuestionBtn);
    }


    private void setupCategoryPanel() {
        //CategoryPanel – Placement
//        mainPanel.add(categoryPanel);
        for (int i = 0; i < categoryButtons.length; i++) {
            catOptionPanel.add(categoryButtons[i] = new JButton());
            categoryButtons[i].setPreferredSize(new Dimension(100, 50));
            categoryButtons[i].setText("");
        }
        categoryPanel.setVisible(true);
        categoryPanel.setBorder(outerPadding);
        categoryPanel.add(catContentPanel, BorderLayout.CENTER);

        choseCategory.setText("Välj kategori:");
        catContentPanel.add(choseCategory, BorderLayout.NORTH);
        catContentPanel.add(catOptionPanel, BorderLayout.CENTER);
        catOptionPanel.setBorder(optionButtonsPadding);
    }

    private void setupQuestionPanel() {
        //QuestionPanel – Placement
//        mainPanel.add(questionPanel);
        questionPanel.setVisible(true);
        questionPanel.setBorder(outerPadding);
        questionPanel.add(header, BorderLayout.NORTH);
        questionPanel.add(quizContentPanel, BorderLayout.CENTER);

        quizContentPanel.add(question, BorderLayout.NORTH);
        quizContentPanel.add(quizOptionPanel, BorderLayout.CENTER);
        quizOptionPanel.setBorder(optionButtonsPadding);

        continueBtn.setPreferredSize(continueBtn.getPreferredSize());
        questionPanel.add(continueBtn, BorderLayout.SOUTH);
        lockContinueButton();

        for (int i = 0; i < optionButtons.length; i++) {
            quizOptionPanel.add(optionButtons[i] = new JButton());
        }

        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setSize(300, 300);
            optionButtons[i].setText("Option " + i);
        }
    }

    //Antopas nu från ClientController.handleQuestionSet() resp. handleCategorySet()
    public void switchPanel(GameState state) {
        switch (state) {
            case NEW_GAME:
                cardLayout.show(mainPanel, "New Game");
                System.out.println("Panel för NEW_GAME (newGamePanel) finns bara i fantasin (men den är fin, tycker Kalle).");
                break;
            case CHOOSE_CATEGORY:
                cardLayout.show(mainPanel, "Category");
                break;
            case ANSWER_QUESTION:
                cardLayout.show(mainPanel, "Question");
                break;
            case SCORE_TABLE:
                cardLayout.show(mainPanel, "Score");
                break;
        }
    }

    public void lockButtons(JButton[] buttonsToLock) {
        for (JButton button : buttonsToLock) {
            button.setEnabled(false);
        }
    }

    public void lockContinueButton() {
        continueBtn.setEnabled(false);
        continueBtn.setBackground(getContentPane().getBackground());
        continueBtn.setOpaque(true);
        continueBtn.setBorder(BorderFactory.createEmptyBorder());
        continueBtn.setForeground(getContentPane().getBackground());
        continueBtn.setText("");
    }

    public void lockScoreButton(JButton button) {
        button.setEnabled(false);
        //button.setVisible(false);
    }

    public void unLockScoreButton(JButton button) {
        button.setEnabled(true);
        //button.setVisible(true);
    }

    public void scoreButtonSetText(JButton button, String text) {
        button.setText(text);
    }

    public void unLockContinueButton() {
        continueBtn.setEnabled(true);
        continueBtn.setBackground(null);
        continueBtn.setOpaque(false);
        continueBtn.setBorder(BorderFactory.createLineBorder(null));
        continueBtn.setForeground(null);
        continueBtn.setText("Fortsätt");
    }

    public void changeColor(JButton buttonToChange, boolean correctAnswer) {
        Color correctColor = new Color(83, 214, 49, 203);
        Color incorrectColor = new Color(225, 52, 123, 203);
        if (correctAnswer) {
            buttonToChange.setBackground(correctColor);
            buttonToChange.setOpaque(true);
        } else {
            buttonToChange.setBackground(incorrectColor);
            buttonToChange.setOpaque(true);
        }

        JFrame rootFrame = (JFrame) SwingUtilities.getWindowAncestor(buttonToChange);
        if (rootFrame != null) {
            Dimension currentSize = rootFrame.getSize();
            rootFrame.setSize(currentSize.width - 1, currentSize.height);  // Shrink width by 1 pixel
            rootFrame.setSize(currentSize.width + 1, currentSize.height);  // Restore original width
        }
    }

    public void changeScoreColor(int[] player1IncomingRes, int[] player2IncomingRes) {
        String correctAnswerLabel = "<html><font color='rgb(83, 214, 49)'>\u25C9</font></html>";
        String wrongAnswerLabel = "<html><font color='rgb(225, 52, 123)'>\u25C9</font></html>";
        String notAnsweredLabel = "<html><font color='rgb(150, 150, 150)'>\u25C9</font></html>";

        assert player1Results.length == player1IncomingRes.length;

        for (int i = 0; i < player1Results.length; i++) {
            if (player1IncomingRes[i] == 1) {
                player1Results[i].setText(correctAnswerLabel);
            } else if (player1IncomingRes[i] == -1) {
                player1Results[i].setText(wrongAnswerLabel);
            } else {
                player1Results[i].setText(notAnsweredLabel);
            }
        }

        for (int i = 0; i < player2Results.length; i++) {
            if (player2IncomingRes[i] == 1) {
                player2Results[i].setText(correctAnswerLabel);
            } else if (player2IncomingRes[i] == -1) {
                player2Results[i].setText(wrongAnswerLabel);
            } else {
                player2Results[i].setText(notAnsweredLabel);
            }
        }
        for (int i = 0; i < roundIndicators.length; i++) {
            roundIndicatorPanel.add(roundIndicators[i] = new JLabel());
            //roundIndicators[i].setPreferredSize(new Dimension(20, 5));
            roundIndicators[i].setText("Round " + (i + 1));
            roundIndicators[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    public JButton getChosenAnswerButton(String chosenAnswer) {
        for (JButton button : optionButtons) {
            if (button.getText().equals(chosenAnswer))
                return button;
        }
        return null;
    }

    public void updateQuestionPanel(String[] questionSet) {
        if (questionSet.length - 1 == optionButtons.length) {
            resetButtonColor(optionButtons);
            question.setText(questionSet[0].trim());
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setEnabled(true);
                optionButtons[i].setText(questionSet[i + 1].trim());
            }
        } else {
            System.err.println("Amount of buttons does not match amount of answer elements");
        }
    }

    public void updateCategoryPanel(String[] categoryChoice) {
        if (categoryButtons.length == categoryChoice.length) {
            for (int i = 0; i < categoryChoice.length; i++) {
                categoryButtons[i].setText(categoryChoice[i]);
                categoryButtons[i].setEnabled(true);
            }
        } else {
            System.err.println("Mismatch concerning amount of buttons and categories");
        }
    }

    public void resetButtonColor(JButton[] buttons) {
        for (JButton button : buttons) {
            button.setBackground(null);
        }
    }

    public void loadProperties(int gameRounds, int questionsPerRound) {
        roundIndicatorPanel = new JPanel(new GridLayout(gameRounds, 1));
        roundIndicators = new JLabel[gameRounds];
        player1ResultsPanel = new JPanel(new GridLayout(gameRounds, questionsPerRound));
        player2ResultsPanel = new JPanel(new GridLayout(gameRounds, questionsPerRound));
        player1Results = new JLabel[gameRounds * questionsPerRound];
        player2Results = new JLabel[gameRounds * questionsPerRound];

        for (int i = 0; i < player1Results.length; i++) {
            player1ResultsPanel.add(player1Results[i] = new JLabel());
            player1Results[i].setPreferredSize(new Dimension(20, 20));
            player1Results[i].setText("<html><font color='rgb(150, 150, 150)'>\u25C9</font></html>");
            player1Results[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
        for (int i = 0; i < player2Results.length; i++) {
            player2ResultsPanel.add(player2Results[i] = new JLabel());
            player2Results[i].setPreferredSize(new Dimension(20, 20));
            player2Results[i].setText("<html><font color='rgb(150, 150, 150)'>\u25C9</font></html>");
            player2Results[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}