import java.net.Socket;

public class Game {
    Socket player1;
    Socket player2;
    final int GAME_ID; //Antar att final är rät iom att ett rum aldrig byter id? /Ylva

    Game(Socket player1Socket, int gameId) {
        this.player1 = player1Socket;
        GAME_ID = gameId;
    }
}
