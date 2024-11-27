import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    PrintWriter printWriter;
    ClientController clientController;

    //develop
    Client(String address, int port, ClientController clientController) {
        this.clientController = clientController;
        new Thread(() -> {
            try (Socket socket = new Socket(address, port);
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                printWriter = new PrintWriter(socket.getOutputStream(), true);

                String serverReply;
                String[] parts;
                while (true) {
                    if ((serverReply = br.readLine()) != null) {
                        //if serverReply starts with gameProperties -> load GUI accordingly
                        if (serverReply.startsWith("GameProperties")) {
                            // Server reply -> "GameProperties: gameRounds, questionsPerRound"
                            //int gameRounds, int questionsPerRound
                            // Handle properties
                            writeToServer("PropertiesReceived");
                        }
                        if (serverReply.startsWith("CategorySet")) {
                            parts = serverReply.split(": ");
                            String categorySet = parts[1];
                            //Låsa upp knapp som byter skärm för ny runda
                            clientController.scoreButtonCategoryMode();
                            //HandleCategorySet ska bara ladda in frågorna i UI. Inte visa panel
                            clientController.handleCategorySet(categorySet);
                            //Låsa upp knapp som byter skärm för ny runda
                        }
                        if (serverReply.startsWith("QuestionSet")) { //+CurrentPlayer
                            //Låser upp knapp för att gå vidare i spel
                            parts = serverReply.split(": ");
                            String questionAndAnswers = parts[1];
                            //Låser upp knapp för att gå vidare i spel
                            clientController.scoreButtonQuestionMode();
                            clientController.handleQuestionSet(questionAndAnswers);
                        }
                        if (serverReply.startsWith("Solution")) {
                            //Update score panel accordingly
                            parts = serverReply.split(":");
                            clientController.handleSolution(parts[1].trim());
                        }
                        if (serverReply.startsWith("PlayerResults")) {
                            parts = serverReply.split(":", 2);
                            clientController.handlePlayerResults(parts[1].trim());
                            //Ylva: Ändra score-skärm
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("IO Error: " + e.getMessage());
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }).start();
    }

    public void writeToServer(String data) {
        printWriter.println(data);
    }
}
