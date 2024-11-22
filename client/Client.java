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
                        if (serverReply.equals("CategorySet")) {
                            parts = serverReply.split(": ");
                            String categorySet = parts[1];
                            clientController.handleCategorySet(categorySet);
                        }
                        if (serverReply.startsWith("QuestionSet")) {
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
                e.printStackTrace();  // Log the exception for debugging
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
