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
                            //HandleCategorySet ska bara ladda in frågorna i UI. Inte visa panel
                            clientController.handleCategorySet(categorySet);
                            //Låsa upp knapp som byter skärm för ny runda
                        }
                        if (serverReply.startsWith("QuestionSet")) {
                            //Låser upp knapp för att gå vidare i spel
                            parts = serverReply.split(": ");
                            String questionAndAnswers = parts[1];
                            clientController.handleQuestionSet(questionAndAnswers);
                        }
                        if (serverReply.startsWith("Solution")) {
                            parts = serverReply.split(":");
                            clientController.handleSolution(parts[1].trim());
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
