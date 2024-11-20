import javax.swing.*;

public class ClientController {
    Client client;
    GUI gui;

    ClientController() {
        int port = 12345;
        String address = "127.0.0.1";
        client = new Client(address, port, this);
        initializeButtonListeners(gui.getOptionButtons());
        startNewGame();

        gui = new GUI();
        gui.gameBoard();
    }

    void initializeButtonListeners(JButton[] answerButtons) {
        for (JButton button : answerButtons) {
            button.addActionListener(_ -> {
                String answer = button.getText();
                client.writeToServer(answer);
                //LOCK BUTTONS
            });
        }
    }

    void notifyGUI(String[] notification) {
        gui.updateGUI(notification);
    }

    void startNewGame() {
        String request = "NewGame";
        client.writeToServer(request);
    }

    public static void main(String[] args) {
        new ClientController();
    }
}
