package tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import jdk.internal.org.jline.utils.InputStreamReader;

public final class App {
    private App() {}

    private static final int SERVER_PORT = 8000;

    public static void main(String[] args) {
        try (ServerSocket listenSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println(String.format("Listening IP: %s:%d", InetAddress.getLocalHost()
                                                                               .toString()
                                                                               .split("/")[1],
                    SERVER_PORT));
            Socket connection = listenSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String clientMessage = "";
            while (clientMessage.equals(".stop") == false) {
                clientMessage = inFromClient.readLine();
                System.out.println(clientMessage);
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }
}
