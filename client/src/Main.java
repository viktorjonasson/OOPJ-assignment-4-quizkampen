public class Main {
    public static void main(String[] args) {
        int port = 12345;
        String address = "127.0.0.1";
        GUI gui = new GUI();
        GameLogic gameLogic = new GameLogic(gui);
        new ClientController(gui, gameLogic, address, port);
    }
}
