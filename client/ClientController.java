import javax.swing.*;
import java.util.Optional;

public class ClientController {
    Client client;
    GUI gui;

    ClientController() {
        int port = 12345;
        String address = "127.0.0.1";
        client = new Client(address, port, this);
        gui = new GUI();
        gui.gameBoard();
        initializeButtonListeners(gui.getOptionButtons());
        startNewGame();
    }

    void initializeButtonListeners(JButton[] answerButtons) {
        for (JButton button : answerButtons) {
            button.addActionListener(_ -> {
                String answer = "Answer: " + button.getText();
                client.writeToServer(answer);
                gui.lockAnswerButtons();
            });
        }
    }

    void notifyGUI(String notification) {
        String[] questionSet = notification.substring(1, notification.length() - 1).split(",\\s*");
        gui.updateGUI(questionSet);
    }

    void startNewGame() {
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

    public static void main(String[] args) {
        new ClientController();
    }
}
