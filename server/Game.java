import java.net.Socket;

public class Game {
    Socket player1;
    Socket player2;
    final int GAME_ID; //Antar att final är rätt iom att ett rum aldrig byter id?
    int curentRound = 1;

    Game(Socket player1Socket, int gameId) {
        this.player1 = player1Socket;
        GAME_ID = gameId;
    }

    public int getCurentRound() {
        return curentRound;
    }
    //Vet inte om vi ska ha dessa två metoder, lägger in dem tillfälligt.
    public void newRound() {
        curentRound++;
    }
}
