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
        Socket clientSocket = new Socket("localhost", 6379);
        Client client = new Client(clientSocket);
        client.interact();
    }


    public void interact() throws IOException {
        Thread responseListenerThread = new Thread(this::listenForResponses);
        responseListenerThread.start();

        Scanner scanner = new Scanner(System.in);

        String response = ""; // Initialize with an empty string
        do {
            System.out.print(socket.getLocalAddress() + "> ");
            String input = scanner.nextLine();
            this.out.write(input.getBytes(StandardCharsets.UTF_8));
            this.out.flush();
        } while (!response.equals("exit"));

        responseListenerThread.interrupt();
        System.out.println("Déconnexion du serveur.");
    }

    private void listenForResponses() {
        try {
            byte[] buffer = new byte[20];
            StringBuilder responseBuilder = new StringBuilder();
            int numRead;

            while ((numRead = this.in.read(buffer)) != -1) {
                responseBuilder.append(new String(buffer, 0, numRead, StandardCharsets.UTF_8));
                if (numRead < buffer.length) {
                    Thread.sleep(100);
                    String response = responseBuilder.toString();
                    System.out.println(response);

                    if (response.equals("exit")) {
                        break;
                    }

                    responseBuilder.setLength(0); // Reset the response builder
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
