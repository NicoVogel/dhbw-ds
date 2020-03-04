package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Hello world!
 */
public final class AppClient {
    private AppClient() {}

    private static final int SERVER_PORT = 8000;
    private static final int PACKAGE_SIZE = 1000;
    private static final String INIT_MESSAGE = "hello world";

    public static void main(String[] args) {

        try (Scanner scan = new Scanner(System.in)) {
            InetAddress aHost = null;

            if (args.length != 0) {
                aHost = tryParseIP(args[0]);
            }

            while (aHost == null) {
                System.out.print("please enter the resciver ip: ");
                aHost = tryParseIP(scan.nextLine());
                if (aHost == null) {
                    System.out.println("no vaild input");
                }
            }

            try (DatagramSocket aSocket = new DatagramSocket()) {
                sendMessage(aSocket, aHost, INIT_MESSAGE);

                String input = "";
                do {
                    input = scan.nextLine();
                    if (input.equals("exit") == false) {
                        sendMessage(aSocket, aHost, input);
                    }
                } while (input.equals("exit") == false);

            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Allgemein: " + e.getMessage());
            }


        } catch (Exception e) {
            System.out.println("hah du noob: " + e.getMessage());
        }
    }

    private static InetAddress tryParseIP(String ip) {
        try {
            return InetAddress.getByName(ip);
        } catch (Exception e) {
            return null;
        }
    }

    private static void sendMessage(DatagramSocket socket, InetAddress host, String msg)
            throws IOException {
        byte[] data = msg.getBytes();
        int dataLenght = data.length;
        DatagramPacket request = new DatagramPacket(data, dataLenght, host, SERVER_PORT);
        socket.send(request);
    }
}

