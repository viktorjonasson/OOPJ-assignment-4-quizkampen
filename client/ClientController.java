import javax.swing.*;

public class ClientController {
    Client client;
    GUI gui;

    ClientController(Client client, GUI gui) {
        this.client = client;
        this.gui = gui;
        initializeButtonListeners(gui.getAnswerButtons());
    }

    void initializeButtonListeners(JButton[] answerButtons) {
        for (JButton button : answerButtons) {
            button.addActionListener(_ -> {
                String answer = button.getText();
                client.sendAnswer(answer);
            });
        }
    }
}
