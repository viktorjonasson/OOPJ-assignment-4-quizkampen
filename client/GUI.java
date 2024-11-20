import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GUI extends JFrame {

    //GUI Setup
    JPanel questionPanel = new JPanel(new BorderLayout(0, 40));
    Border outerPadding = BorderFactory.createEmptyBorder(20, 40, 30, 40);
    JPanel quizContentPanel = new JPanel(new BorderLayout());
    Border optionButtonsPadding = BorderFactory.createEmptyBorder(20, 0, 20, 0);
    JPanel quizOptionPanel = new JPanel(new GridLayout(2, 2));

    JLabel header = new JLabel("This kategori – Fråga N", SwingConstants.CENTER);
    JLabel question = new JLabel();
    JButton[] optionButton = new JButton[4];
    JButton continueBtn = new JButton("Fortsätt");

    //Getter for buttons
    public JButton[] getOptionButtons() {
        return optionButton;
    }

    public void gameBoard() {
        //GUI – Placement
        this.add(questionPanel);
        questionPanel.setBorder(outerPadding);
        questionPanel.add(header, BorderLayout.NORTH);
        questionPanel.add(quizContentPanel, BorderLayout.CENTER);
        quizContentPanel.add(question, BorderLayout.NORTH);
        quizContentPanel.add(quizOptionPanel, BorderLayout.CENTER);
        quizOptionPanel.setBorder(optionButtonsPadding);

        questionPanel.add(continueBtn, BorderLayout.SOUTH);

        //GUI – Set content
        question.setText("<HTML>Hur många YH-poäng är Nackademins utbildning i Javaprogrammering?</HTML>");
        initOptionButton();

        //GUI – Display it
        setSize(500, 700);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void initOptionButton() {
        for (int i = 0; i < optionButton.length; i++) {
            quizOptionPanel.add(optionButton[i] = new JButton());
        }
        setOptionButton();
    }

    public void setOptionButton() {
        for (int i = 0; i < optionButton.length; i++) {
            optionButton[i].setText("Option " + i);
        }
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

    public void changeText(
            JButton[] answerButtons, JLabel questionLabel, String[] answers, String question) {
        if (answerButtons.length == answers.length) {
            for (int i = 0; i < answerButtons.length; i++) {
                answerButtons[i].setText(answers[i]);
            }
            questionLabel.setText(question);
        } else {
            System.err.println("Amount of buttons does not match amount of answer elements");
        }
    }
}


