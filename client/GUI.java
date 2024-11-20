import javax.swing.*;
import java.awt.*;

public class GUI {
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
