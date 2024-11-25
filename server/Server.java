import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final GameHandler gameHandler;
    private ServerSocket serverSocket;
    Server(int port) {
        gameHandler = new GameHandler(this);
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                if (gameHandler.getPendingGames().isEmpty()) {
                    gameHandler.createNewGame(serverSocket.accept());
                } else {
                    gameHandler.connectPlayerToGame(serverSocket.accept());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (serverSocket != null) {
                try{
                    serverSocket.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        new Server(port);
    }
}
