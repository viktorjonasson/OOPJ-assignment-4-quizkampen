import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final GameHandler gameHandler;
    private PrintWriter printWriter;

    Server(int port) {
        gameHandler = new GameHandler(this);
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            String request;
            String[] parts;
            while (true) {
                if ((request = bufferedReader.readLine()) != null) {
                    if (request.startsWith("NewGame")) {
                        gameHandler.createNewGame(clientSocket);
                    }
                    if (request.startsWith("Answer")) {
                        parts = request.split(":");
                        gameHandler.checkAnswer(parts[1].trim());
                    }
                    if (request.startsWith("Category")) {
                        parts = request.split(":");
                        gameHandler.handleQuestionSet();
                    }
                }
            }

        } catch (IOException e) {
            //TO DO: FIX THIS
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    protected void writeToClient(String data) {
        printWriter.println(data);
    }

    public static void main(String[] args) {
        int port = 12345;
        new Server(port);
    }
}
