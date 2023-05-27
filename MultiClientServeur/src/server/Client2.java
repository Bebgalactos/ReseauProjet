package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client2 {
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public Client2(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost", 1337);
        Client2 client = new Client2(clientSocket);
        client.interact();
    }

    public void interact() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("You> ");
            final String input = scanner.nextLine();
            this.out.write(input.getBytes(StandardCharsets.UTF_8));
            this.out.flush();

            byte[] buffer = new byte[20];
            int numRead;

            StringBuilder responseBuilder = new StringBuilder();
            while ((numRead = this.in.read(buffer)) != -1) {
                responseBuilder.append(new String(buffer, 0, numRead, StandardCharsets.UTF_8));
                if (numRead < buffer.length) {
                    break;
                }
            }

            String response = responseBuilder.toString();
            System.out.println("Server> " + response);

            if (response.equals("exit")) {
                System.out.println("Déconnexion du serveur.");
                break;
            }
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
