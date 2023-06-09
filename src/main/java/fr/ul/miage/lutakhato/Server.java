package fr.ul.miage.lutakhato;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static boolean slave = false;
    public static List<Socket> slaves = new ArrayList<>();
    protected static List<Channel> channels = new ArrayList();

    public Server() {
    }

    public static void main(String[] args) {
        (new Server()).start();
    }

    private void start() {
        try {
            InetAddress bindAddress = InetAddress.getByName("0.0.0.0");
            ServerSocket server = new ServerSocket(6379, 1, bindAddress);
            System.out.println("> Attente de connexion des clients ...");

            while(true) {
                Socket clientSocket = server.accept();
                Client client = new Client(clientSocket);
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                System.out.println("> server.Client connecté: " + clientAddress);
                (new ServerThread(client)).start();
            }
        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}
