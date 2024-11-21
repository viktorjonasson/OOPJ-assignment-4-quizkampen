import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private GameHandler gameHandler;
    private PrintWriter printWriter;

    Server(String address, int port) {
        gameHandler = new GameHandler();
        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket clientSocket = serverSocket.accept();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());) {

            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);

            Object request;
            String[] parts;

            while (true) {
                if ((request = objectInputStream.readObject()) != null){

                    if (request instanceof String) {
                        if (((String) request).startsWith("NewGame"))
                            gameHandler.createNewGame(clientSocket, this);

                        if (((String) request).startsWith("Answer")) {
                            parts = ((String) request).split(":");
                            gameHandler.checkAnswer(parts[1].trim());
                        }
                    }
                }
            }

        } catch (IOException e) {
            //TO DO: FIX THIS
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
        String address = "127.0.0.1";
        new Server(address, port);
    }
}
