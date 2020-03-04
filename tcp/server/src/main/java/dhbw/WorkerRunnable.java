package dhbw;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class WorkerRunnable implements Runnable, Observer {
    private static final String MSG_ENTER = "enters lobby";
    private static final String MSG_LEAVE = "leaves lobby";
    private static final String MSG_STOP = "lobby is closed";
    private static final String CMD_LEAVE = ".stop";
    private static final String CMD_STOP = ".stopall";

    private Socket clientSocket;
    private String nickname = "";
    private String lastSentMessage = "";
    private OutputStream publicOutput = null;
    private ServerOperations serverOperations;
    private PostJob postJob;

    public WorkerRunnable(Socket clientSocket, ServerOperations serverOperations, PostJob postJob) {
        this.clientSocket = clientSocket;
        this.serverOperations = serverOperations;
        this.postJob = postJob;
    }

    public Closeable getCloseable() {
        return this.clientSocket;
    }

    public void run() {
        try (BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream output = clientSocket.getOutputStream()) {

            publicOutput = output;
            sendToClient("Please enter a nickname: ");
            this.nickname = inFromClient.readLine();

            this.serverOperations.getChat()
                                 .addMessage(this.nickname, MSG_ENTER);
            this.serverOperations.getChat()
                                 .addObserver(this);


            String clientMessage = "";
            while (clientMessage.equals(CMD_LEAVE) == false) {

                if (this.serverOperations.getIsStopped()) {
                    break;
                }

                clientMessage = inFromClient.readLine();
                if (clientMessage.equals((CMD_STOP))) {
                    clientMessage = MSG_STOP;
                    this.serverOperations.stop();
                }

                String sendMessage = clientMessage.equals(CMD_LEAVE) ? MSG_LEAVE : clientMessage;
                this.serverOperations.getChat()
                                     .addMessage(this.nickname, sendMessage,
                                             (msg) -> this.lastSentMessage = msg);
            }
        } catch (IOException e) {
            if (this.serverOperations.getIsStopped()) {
                return;
            }
            System.out.println(String.format("Error while running: %s", e.getMessage()));
        } finally {
            this.serverOperations.getChat()
                                 .deleteObserver(this);

            if (this.serverOperations.getIsStopped()) {
                this.serverOperations.closeAll();
            }
        }
        this.serverOperations.ensureClose(this.clientSocket);
        this.postJob.postRun(this);
    }

    private void sendToClient(String message) {
        try {
            this.publicOutput.write(message.getBytes());
            this.publicOutput.flush();
        } catch (Exception e) {
            System.out.println(String.format("Error while output: %s", e.getMessage()));
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String lastMessage = arg.toString();
        if (this.lastSentMessage.equals(lastMessage) == false) {
            sendToClient(lastMessage);
        }
    }
}
