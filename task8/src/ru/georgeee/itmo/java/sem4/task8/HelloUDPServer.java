package ru.georgeee.itmo.java.sem4.task8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.*;

/**
 * Created by georgeee on 23.04.14.
 */
public class HelloUDPServer extends Thread {
    private static final int SOCKET_TIMEOUT = 1000;
    private static final int MAX_PACKET_SIZE = 65536;
    private static final int THREAD_COUNT = 10;
    private static final int REQUEST_CAPACITY = 1000;
    private final int serverPort;

    public HelloUDPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("HelloUDPServer receives exactly one argument: port\n");
            System.exit(1);
            return;
        }
        int port;
        try {
            port = Integer.parseInt(args[0]);
            if (port <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            System.err.println("serverPort must be a positive number");
            System.exit(1);
            return;
        }
        HelloUDPServer server = new HelloUDPServer(port);
        server.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            in.readLine();
        } catch (IOException e) {
        } finally {
            server.interrupt();
        }
    }

    void sendRequest(DatagramSocket socket, String request, InetAddress address, int port) throws IOException {
        byte[] bytes = request.concat("\0").getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
        socket.send(packet);
    }

    String getResponseMessage(DatagramPacket receivePacket) {
        byte buffer[] = receivePacket.getData();
        int index = 0;
        while (buffer[index] != 0) {
            ++index;
        }
        return new String(buffer, 0, index);
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        ExecutorService service = null;
        try {
            serverSocket = new DatagramSocket(serverPort);
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>(REQUEST_CAPACITY);
            service = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT,
                    0L, TimeUnit.MILLISECONDS,
                    blockingQueue);
            while (!isInterrupted()) {
                byte[] buffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                try {
                    serverSocket.receive(receivePacket);
                    service.submit(new ServerTask(serverSocket, receivePacket));
                } catch (IOException e) {
                }
            }
            interrupt();
        } catch (SocketException e) {
            System.err.println("Can't open socket for server (serverPort: " + serverPort + ")");
            System.exit(1);
            return;
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (service != null) {
                service.shutdownNow();
            }
        }
    }


    class ServerTask implements Runnable {
        private final DatagramSocket serverSocket;
        private final DatagramPacket receivePacket;

        public ServerTask(DatagramSocket serverSocket, DatagramPacket receivePacket) {
            this.serverSocket = serverSocket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            String message = getResponseMessage(receivePacket);
            InetAddress address = receivePacket.getAddress();
            int port = receivePacket.getPort();
            try {
                sendRequest(serverSocket, ("Hello, ").concat(message), address, port);
            } catch (IOException e) {
                System.err.println("Response to message " + message + " failed to be sent");
            }
        }
    }
}
