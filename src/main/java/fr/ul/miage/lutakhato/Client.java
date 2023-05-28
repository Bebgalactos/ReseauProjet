package fr.ul.miage.lutakhato;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public Client(Socket socket) {
        this.socket = socket;
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Socket not connected");
        }
    }

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost", 1337);
        Client client = new Client(clientSocket);
        client.interact();
    }

    public void interact() throws IOException {
        Scanner scanner = new Scanner(System.in);

        String response;
        do {
            System.out.print(socket.getLocalAddress() + "> ");
            String input = scanner.nextLine();
            this.out.write(input.getBytes(StandardCharsets.UTF_8));
            this.out.flush();
            byte[] buffer = new byte[20];
            StringBuilder responseBuilder = new StringBuilder();

            int numRead;
            while((numRead = this.in.read(buffer)) != -1) {
                responseBuilder.append(new String(buffer, 0, numRead, StandardCharsets.UTF_8));
                if (numRead < buffer.length) {
                    break;
                }
            }

            response = responseBuilder.toString();
            System.out.println("Server> " + response);
        } while(!response.equals("exit"));
/*
        try {
            Socket client = new Socket("localhost", 1337);
            InputStream in = client.getInputStream();
            OutputStream os = client.getOutputStream();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print(client.getLocalAddress() + "> ");
                final String input = scanner.next();
                os.write(input.getBytes(StandardCharsets.UTF_8));

                byte[] buffer = new byte[20];
                int numRead;

                StringBuilder responseBuilder = new StringBuilder();
                while ((numRead = in.read(buffer)) != -1) {
                    responseBuilder.append(new String(buffer, 0, numRead, StandardCharsets.UTF_8));
                    if (numRead < buffer.length) {
                        break;
                    }
                }

                String response = responseBuilder.toString();
                System.out.println("> " + response);

                if (response.equals("exit")) {
                    System.out.println("Déconnexion du serveur.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }*/

        System.out.println("Déconnexion du serveur.");
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }
}
