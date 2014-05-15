package ru.georgeee.itmo.java.sem4.task8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class HelloUDPClient {
    public static final int SUBCLIENT_COUNT = 10;
    private final SubClient[] subClients;

    public HelloUDPClient(InetAddress serverAddress, int serverPort, String prefix) {
        subClients = new SubClient[SUBCLIENT_COUNT];
        for (int i = 0; i < SUBCLIENT_COUNT; ++i) {
            subClients[i] = new SubClient(i, serverAddress, serverPort, prefix);
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("HelloUDPClient receives exactly 3 arguments: server's host name and port, prefix string\n");
            System.exit(1);
            return;
        }
        String serverName = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if (port <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            System.err.println("port must be a positive number");
            System.exit(1);
            return;
        }
        InetAddress serverAddress = null;
        try {
            serverAddress = InetAddress.getByName(serverName);
        } catch (UnknownHostException e) {
            System.err.println("unknown host: " + serverName);
            System.exit(1);
        }
        String prefix = args[2];
        HelloUDPClient client = new HelloUDPClient(serverAddress, port, prefix);
        client.launch();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            in.readLine();
        } catch (IOException e) {
        } finally {
            client.stop();
        }
    }

    public void stop() {
        for (SubClient client : subClients) {
            client.interrupt();
        }
    }

    public void launch() {
        for (SubClient client : subClients) {
            client.start();
        }
    }

    private static class SubClient extends Thread {
        private static final int MAX_PACKET_SIZE = 65536;
        private byte[] responseBuffer = new byte[MAX_PACKET_SIZE];
        private static final int SOCKET_TIMEOUT = 1000;
        private final InetAddress serverAddress;
        private final int serverPort;
        private final int subClientId;
        private final String prefix;
        private int reqId = 0;

        private SubClient(int subClientId, InetAddress serverAddress, int serverPort, String prefix) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.subClientId = subClientId;
            this.prefix = prefix;
        }

        String readResponse(DatagramPacket receivePacket) throws IOException {
            byte buffer[] = receivePacket.getData();
            int index = 0;
            while (buffer[index] != 0) {
                ++index;
            }
            return new String(buffer, 0, index);
        }

        @Override
        public void run() {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket();
                socket.setSoTimeout(SOCKET_TIMEOUT);
                interruptLabel:
                while (!isInterrupted()) {
                    String request = prefix.concat("_").concat(String.valueOf(subClientId))
                            .concat("_").concat(String.valueOf(reqId++));
                    try {
                        byte[] bytes = request.concat("\0").getBytes();
                        DatagramPacket requestPacket = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
                        socket.send(requestPacket);
                        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                        while (true) {
                            if (isInterrupted()) {
                                break interruptLabel;
                            }
                            try {
                                socket.receive(responsePacket);
                                break;
                            } catch (SocketTimeoutException e) {
                                socket.send(requestPacket);
                                System.err.println("Request " + request + " timeouted, resending");
                                continue;
                            }
                        }
                        String response = readResponse(responsePacket);
                        System.out.println(">> " + request + "\n<< " + response + "\n");
                    } catch (IOException e) {
                        System.err.println("Request " + request + " failed to be sent");
                    }
                }
                interrupt();
            } catch (SocketException e) {
                System.err.println("Failed to opened socket for client #" + subClientId);
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
