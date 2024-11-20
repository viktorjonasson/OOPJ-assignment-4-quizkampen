import javax.swing.*;

public class ClientController {
    Client client;
    GUI gui;

    ClientController() {
        int port = 12345;
        String address = "127.0.0.1";
        gui = new GUI();
        gui.gameBoard();
        client = new Client(address, port);
        initializeButtonListeners(gui.getOptionButtons());
        startNewGame();
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

    void startNewGame() {
        String request = "NewGame";
        client.writeToServer(request);
    }

    public static void main(String[] args) {
        new ClientController();
    }
}
