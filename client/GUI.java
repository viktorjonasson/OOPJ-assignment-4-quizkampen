import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;

public class GUI extends JFrame {

    //GUI Setup
    Border outerPadding = BorderFactory.createEmptyBorder(20, 40, 30, 40);
    Border optionButtonsPadding = BorderFactory.createEmptyBorder(20, 0, 20, 0);

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
        setupQuestionPanel();
        setupCategoryPanel();

        //Ska förhoppningsvis kunna tas bort. Annars sätt annat default-värde.
        question.setText("<HTML>Hur många YH-poäng är Nackademins utbildning i Javaprogrammering?</HTML>");

        //GUI – Display it
        setSize(500, 700);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupCategoryPanel() {
        //CategoryPanel – Placement
        this.add(categoryPanel);
        categoryPanel.setVisible(false);
        categoryPanel.setBorder(outerPadding);
        categoryPanel.add(catContentPanel, BorderLayout.CENTER);

        choseCategory.setText("Välj kategori:");
        catContentPanel.add(choseCategory, BorderLayout.NORTH);
        catContentPanel.add(catOptionPanel, BorderLayout.CENTER);
        catOptionPanel.setBorder(optionButtonsPadding);

        for (int i = 0; i < categoryButtons.length; i++) {
            catOptionPanel.add(categoryButtons[i] = new JButton());
            categoryButtons[i].setSize(300, 100);
            categoryButtons[i].setText("");
        }
    }

    private void setupQuestionPanel() {
        //QuestionPanel – Placement
        this.add(questionPanel);
        questionPanel.setVisible(false);
        questionPanel.setBorder(outerPadding);
        questionPanel.add(header, BorderLayout.NORTH);
        questionPanel.add(quizContentPanel, BorderLayout.CENTER);

        quizContentPanel.add(question, BorderLayout.NORTH);
        quizContentPanel.add(quizOptionPanel, BorderLayout.CENTER);
        quizOptionPanel.setBorder(optionButtonsPadding);

        questionPanel.add(continueBtn, BorderLayout.SOUTH);

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
        //Tänker att man inleder med att nollställa synlighet på alla paneler, men det finns kanske bättre sätt?
        categoryPanel.setVisible(false);
        questionPanel.setVisible(false);

        switch (state) {
            case NEW_GAME:
                System.out.println("Panel NEW_GAME finns ännu inte");
                break;
            case CHOOSE_CATEGORY:
                categoryPanel.setVisible(true);
                break;
            case ANSWER_QUESTION:
                questionPanel.setVisible(true);
                break;
            case SCORE_TABLE:
                System.out.println("Panel SCORE_TABLE finns ännu inte.");
        }
        repaint();
        revalidate();
    }

    public void lockButtons(JButton[] buttonsToLock) {
        for (JButton button : buttonsToLock) {
            button.setEnabled(false);
        }
    }

    public void lockContinueButton() {
        continueBtn.setEnabled(false);
        continueBtn.setVisible(false);
    }

    public void unLockContinueButton() {
        continueBtn.setEnabled(true);
        continueBtn.setVisible(true);
    }

    public void changeColor(JButton buttonToChange, boolean correctAnswer) {
        Color correctColor = new Color(83, 214, 49, 203);
        Color incorrectColor = new Color(225, 52, 123, 203);
        if (correctAnswer) {
            buttonToChange.setBackground(correctColor);
        } else {
            buttonToChange.setBackground(incorrectColor);
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
            }
        } else {
            System.err.println("Mismatch concerning amount of buttons and categories");
        }
    }
}