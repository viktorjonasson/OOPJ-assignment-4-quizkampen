import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    //develop
    Client(String adress, int port)  {
        try (Socket socket = new Socket(adress, port);
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            requestAnswers(printWriter, "NewGame");

            String answer;
            while (true) {
                if ((answer = br.readLine()) != null) {
                    System.out.println(answer);
                }
            }
        } catch (IOException e) {
            //TO DO: Handle exception
        }
    }

    private void requestAnswers(PrintWriter printWriter, String request) {
        printWriter.write(request);
    }

    public static void main(String[] args) {
        int port = 12345;
        String address = "127.0.0.1";
        new Client(address, port);
    }
}
