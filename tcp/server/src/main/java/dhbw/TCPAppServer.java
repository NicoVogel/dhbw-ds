package dhbw;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class TCPAppServer implements ServerOperations {
    private TCPAppServer() {}


    public static void main(final String[] args) {
        new TCPAppServer().startServer();
    }

    private static final int SERVER_PORT = 8000;

    private boolean isStopped = false;
    private List<Closeable> closeableReferences = new ArrayList<>();
    private Chatserver chat;

    @Override
    public boolean getIsStopped() {
        return this.isStopped;
    }


    @Override
    public void stop() {
        this.isStopped = true;
    }


    @Override
    public Chatserver getChat() {
        if (this.chat == null) {
            this.chat = new Chatserver();
        }
        return this.chat;
    }

    public void startServer() {
        try (ServerSocket listenSocket = new ServerSocket(SERVER_PORT)) {
            this.closeableReferences.add(listenSocket);
            String ip = InetAddress.getLocalHost()
                                   .toString()
                                   .split("/")[1];
            System.out.println(String.format("Listening IP: %s:%d", ip, SERVER_PORT));

            while (isStopped == false) {
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

                closeableReferences.add(clientSocket);
                new Thread(new WorkerRunnable(clientSocket, this, (runner) -> {
                    closeableReferences.remove(runner.getCloseable());
                })).start();
            }
        } catch (final IOException e) {
            System.out.println("Listen :" + e.getMessage());
        } finally {
            closeAll();
        }
    }

    public void closeAll() {
        for (int i = this.closeableReferences.size(); i >= 0; i--) {
            try {
                ensureClose(this.closeableReferences.get(i));
            } catch (Exception e) {
            }
        }
    }

    public void ensureClose(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
        }
    }

}
