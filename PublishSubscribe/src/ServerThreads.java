import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class ServerThreads {

    public static void main(String[] args) throws IOException {
        new ServerThreads().start();
    }

    private void start() {
        try {
            InetAddress bindAddress = InetAddress.getByName("0.0.0.0");
            ServerSocket server = new ServerSocket(1337, 1, bindAddress);
            System.out.println("> Attente de connexion des clients ...");

            while (true) {

                    final Socket client = server.accept();
                    String clientAddress = client.getInetAddress().getHostAddress();
                    System.out.println("> Client connecté: " + clientAddress);
                    new SubscriberThread(client).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SubscriberThread extends Thread {
        private final Socket client;

        public SubscriberThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                while (true) {
                    byte[] buffer = new byte[20];
                    int numRead = in.read(buffer);

                    if (numRead == -1) {
                        break;
                    }

                    String str = new String(buffer, 0, numRead, StandardCharsets.UTF_8);
                    System.out.println("> reçu :  " + str);

                    out.write(str.toUpperCase().getBytes(StandardCharsets.UTF_8));
                }

                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
