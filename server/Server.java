import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Server {
    private Queue<Game> pendingGames;
    private List<Game> ongoingGames;
    private int GameID = 0;
    private ServerSocket serverSocket;

    Server(int port) {
        this.pendingGames = new LinkedList<>();
        this.ongoingGames = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                if (pendingGames.isEmpty()) {
                    createNewGame(serverSocket.accept());
                } else {
                    connectPlayerToGame(serverSocket.accept());
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

    public void createNewGame(Socket incomingConnection) throws IOException {
        ++GameID;
        Game newGameRoom = new Game(incomingConnection, GameID);
        pendingGames.add(newGameRoom);
        //Send amount of rounds
//        newGameRoom.sendGameProperties(1);
    }

    public boolean connectPlayerToGame(Socket playerConnection) {
        try {
            Game tempGame = pendingGames.poll();
            if (tempGame != null) {
                tempGame.player2 = playerConnection;
                ongoingGames.add(tempGame);
                tempGame.writerPlayer2 = new PrintWriter(playerConnection.getOutputStream(), true);
                tempGame.player2Initiated = true;
                tempGame.sendGameProperties(2);
            }
            //Send amount of rounds
            return true;
        } catch (NullPointerException | IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        new Server(port);
    }
}
