import java.net.Socket;

public class Game {
    Socket player1;
    Socket player2;

    Game(Socket player1Socket) {
        this.player1 = player1Socket;
    }
}
