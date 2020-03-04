package dhbw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public final class TCPAppServer {
    private TCPAppServer() {}


    public static void main(final String[] args) {
        new TCPAppServer().startServer();
    }

    private static final int SERVER_PORT = 8000;
    private boolean isStopped = false;
    private List<Thread> threads = new ArrayList<>();

    private Chatserver chat = new Chatserver();

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



    private class WorkerRunnable implements Runnable, Observer {
        private Socket clientSocket = null;
        private String nickname = "";
        private String lastSentMessage = "";
        private OutputStream publicOutput = null;

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
                publicOutput = output;
                chat.addObserver(this);

                String clientMessage = "";
                while ((clientMessage.equals(".stop") == false)) {
                    clientMessage = inFromClient.readLine();
                    this.lastSentMessage = chat.addMessage(this.nickname, clientMessage);
                    chat.notifyOther();
                }
            } catch (IOException e) {
                System.out.println(String.format("Error while running: %s", e.getMessage()));
            } finally {
                chat.deleteObserver(this);
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            try {
                String lastMessage = arg.toString();
                if (this.lastSentMessage.equals(lastMessage) == false) {
                    this.publicOutput.write(lastMessage.getBytes());
                    this.publicOutput.flush();
                }
            } catch (IOException e) {
                System.out.println(String.format("Error while output: %s", e.getMessage()));
            }
        }
    }
}
