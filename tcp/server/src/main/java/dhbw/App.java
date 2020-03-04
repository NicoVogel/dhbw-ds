package dhbw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class App {
    private App() {}

    private static final int SERVER_PORT = 8000;

    public static void main(final String[] args) {
        try (ServerSocket listenSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println(String.format("Listening IP: %s:%d", InetAddress.getLocalHost()
                                                                               .toString()
                                                                               .split("/")[1],
                    SERVER_PORT));
            final Socket connection = listenSocket.accept();
            final BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String clientMessage = "";
            while (clientMessage.equals(".stop") == false) {
                clientMessage = inFromClient.readLine();
                System.out.println(clientMessage);
            }
        } catch (final IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}
