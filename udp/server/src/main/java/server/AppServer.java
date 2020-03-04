package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Hello world!
 */
public final class AppServer {
    private AppServer() {}

    private static final int SERVER_PORT = 8000;
    private static final int PACKAGE_SIZE = 1000;

    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(SERVER_PORT);
            byte[] buffer = null;
            while (true) {
                buffer = new byte[PACKAGE_SIZE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                System.out.println(String.format("%s: %s", request.getAddress()
                                                                  .toString(),
                        new String(request.getData(), 0, request.getData().length)));
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }

    }
}

