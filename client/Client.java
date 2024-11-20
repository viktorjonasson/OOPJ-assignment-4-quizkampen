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

    public void writeToServer(String data) {
        printWriter.println(data);
    }


}
