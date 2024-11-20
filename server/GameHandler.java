import java.net.Socket;
import java.util.*;

public class GameHandler {
    private Queue<Game> pendingGames;
    private List<Game> ongoingGames;
    private DataBase db;
    private Server server;
    private int GameID = 0;

    public void createNewGame(Socket incomingConnection, Server server) {
        this.server = server;
        this.db = new DataBase();
        this.pendingGames = new LinkedList<>();
        this.ongoingGames = new ArrayList<>();
        ++GameID;
        Game newGameRoom = new Game(incomingConnection, GameID);
        pendingGames.add(newGameRoom);
        server.writeToClient(Arrays.toString(db.getQuestionSet()));
    }

    public boolean connectPlayerToGame(Socket playerConnection, Game game) {
        try {
            game.player2 = playerConnection;
            ongoingGames.add(game);
            pendingGames.remove(game);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }
}
