import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    PrintWriter printWriter;
    //develop
    Client(String adress, int port)  {
        try (Socket socket = new Socket(adress, port);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            printWriter = new PrintWriter(socket.getOutputStream(), true);

            requestAnswers(printWriter, "NewGame");
            String answer;
            while (true) {
                if ((answer = br.readLine()) != null) {
                    System.out.println(answer);
                }
            }
        } catch (IOException e) {
            //TO DO: Handle exception
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    private void requestAnswers(PrintWriter printWriter, String request) {
        printWriter.println(request);
    }

    public void sendAnswer(String answer) {
        printWriter.println(answer);
    }

    public static void main(String[] args) {
        int port = 12345;
        String address = "127.0.0.1";
        new Client(address, port);
    }
}
