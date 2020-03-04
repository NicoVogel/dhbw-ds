package dhbw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class TCPAppServer {
    private TCPAppServer() {}


    public static void main(final String[] args) {
        new TCPAppServer().startServer();
    }

    private static final int SERVER_PORT = 8000;
    private boolean isStopped = false;
    private List<Thread> threads = new ArrayList<>();

    public void startServer() {
        try (ServerSocket listenSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println(String.format("Listening IP: %s:%d", InetAddress.getLocalHost()
                                                                               .toString()
                                                                               .split("/")[1],
                    SERVER_PORT));

            while (!isStopped) {
                Socket clientSocket = null;
                try {
                    clientSocket = listenSocket.accept();
                } catch (IOException e) {
                    if (isStopped) {
                        System.out.println("Server Stopped.");
                        return;
                    }
                    throw new RuntimeException("Error accepting client connection", e);
                }

                Thread newConnection = new Thread(new WorkerRunnable(clientSocket));
                threads.add(newConnection);
                newConnection.start();
            }
        } catch (final IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

    public void addMessage(String nickname, String message) {
        System.out.println(String.format("%s: %s", nickname, message));
    }

    private class WorkerRunnable implements Runnable {
        private Socket clientSocket = null;
        private String nickname = "";

        public WorkerRunnable(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try (BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    OutputStream output = clientSocket.getOutputStream()) {

                output.write(("Please enter a nickname: ").getBytes());
                output.flush();

                this.nickname = inFromClient.readLine();

                String clientMessage = "";
                while (clientMessage.equals(".stop") == false) {
                    clientMessage = inFromClient.readLine();
                    addMessage(this.nickname, clientMessage);
                }

            } catch (IOException e) {
                // report exception somewhere.
                e.printStackTrace();
            }
        }
    }
}
