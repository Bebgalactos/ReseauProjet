import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
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
                System.out.println("< " + response);

                if (response.equals("exit")) {
                    System.out.println("Déconnexion du serveur.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
