import java.net.Socket;
import java.util.*;

public class GameHandler {
    private Queue<Game> pendingGames;
    private List<Game> ongoingGames;
    private DataBase db;
    private Server server;
    private int GameID = 0;

    GameHandler(Server server) {
        this.server = server;
        this.db = new DataBase();
        this.pendingGames = new LinkedList<>();
        this.ongoingGames = new ArrayList<>();
    }

    public void createNewGame(Socket incomingConnection, Server server) {
        ++GameID;
        Game newGameRoom = new Game(incomingConnection, GameID);
        pendingGames.add(newGameRoom);
        handleCategorySet();
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

    public void handleCategorySet() {
        String reply = "CategorySet: " + Arrays.toString(db.getCategorySet());
        server.writeToClient(reply);
    }

    public void handleQuestionSet() {
        String reply = "QuestionSet: " + Arrays.toString(db.getQuestionSet());
        server.writeToClient(reply);
    }

    void checkAnswer(String answer) {
        String reply = "Solution: ";
        if (answer.equals("Option 1")) {
            reply += answer + ", true";
        } else {
            reply += answer + ", false";
        }
        server.writeToClient(reply);
    }
}
