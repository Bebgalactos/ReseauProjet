package server;

import user.Channel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Server {

    protected static List<Channel> channels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        channels.add(new Channel("redis"));
        new Server().start();
    }

    private void start() {
        try {
            InetAddress bindAddress = InetAddress.getByName("0.0.0.0");
            ServerSocket server = new ServerSocket(1337, 1, bindAddress);
            System.out.println("> Attente de connexion des clients ...");

            while (true) {
                final Socket clientSocket = server.accept();
                Client client = new Client(clientSocket);  // construct a Client with a Socket

                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("> server.Client connecté: " + clientAddress);

                new SubscriberThread(client).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
