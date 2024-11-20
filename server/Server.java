import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private GameHandler gameHandler;
    private PrintWriter printWriter;

        //develop
    Server(String address, int port) {
        gameHandler = new GameHandler();
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            String request;
            String[] serverAnswer;

            while (true) {
                if ((request = bufferedReader.readLine()) != null) {
                    if (request.equalsIgnoreCase("NewGame")) {
                        gameHandler.createNewGame(clientSocket, this);
                    }
                }
            }
        } catch (IOException e) {
            //TO DO: FIX THIS
        }
    }

    protected void writeToClient(String data) {
        printWriter.println(data);
    }

    public static void main(String[] args) {
        int port = 12345;
        String address = "127.0.0.1";
        new Server(address, port);
    }
}
