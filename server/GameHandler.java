import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class GameHandler {
    private Queue<Game> pendingGames;
    private List<Game> ongoingGames;

    private Server server;
    private int GameID = 0;

    GameHandler(Server server) {
        this.server = server;
        this.pendingGames = new LinkedList<>();
        this.ongoingGames = new ArrayList<>();
    }

    public Queue<Game> getPendingGames() {
        return pendingGames;
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
}
