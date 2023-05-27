package server;

import user.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;

public class SubscriberThread extends Thread {
    private final Client client;
    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    public static String[] keyWords = new String[]{"PUBLISH", "SUBSCRIBE", "UNSUBSCRIBE", "APPEND", "DECR", "DEL", "EXISTS", "EXPIRE", "GET", "INCR", "SET"};

    public SubscriberThread(Client client) {
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

                // Traitement fait pour envoyer une reponse
                //out.write(str.toUpperCase().getBytes(StandardCharsets.UTF_8));
                this.syntaxCheck(str);
            }

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void syntaxCheck(String entry) throws IOException {

        String[] entryParts = entry.split(" ");

        // Vérification de l'existence de la requête
        String firstKeyword = entryParts[0].trim();
        boolean requestExists = Arrays.stream(keyWords).anyMatch(keyword -> keyword.equals(firstKeyword.toUpperCase()));
        if (!requestExists) {
            return ;
        }

        // Vérification des paramètres
        switch (firstKeyword) {
            case "PUBLISH":
                this.publishToChannel(entryParts[1].trim(), entryParts[2].trim());
                return ;
            case "SUBSCRIBE":
                this.subscribeToChannel(entryParts[1].trim());
                return ;
            case "UNSUBSCRIBE":
                return ;
            case "APPEND":
                return ;
            case "INCR":
                return ;
            case "DECR":
                return ;
            case "GET":
                return ;
            case "DEL":
                return ;
            case "EXISTS":
                return ;
            case "EXPIRE":
                return ;
            case "SET":
                return ;
            default:
                return ;
        }
    }

    private void subscribeToChannel(String channelName) throws IOException {
        Channel c = Channel.findChannelInList(Server.channels, channelName);
        if(c != null){
            c.subscribe(this);
            this.client.getOutputStream().write(("Subscribed to " + channelName + " Channel").toUpperCase().getBytes(StandardCharsets.UTF_8));
        }else{
            this.client.getOutputStream().write(("Channel " + channelName + " not found !").toUpperCase().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void publishToChannel(String channelName, String message) throws IOException {
        Channel c = Channel.findChannelInList(Server.channels, channelName);
        if(c != null){
            c.publish(message);
        }else{
            this.client.getOutputStream().write("Channel not found !".toUpperCase().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void NewMessage(String message) throws IOException {
        this.client.getOutputStream().write(message.toUpperCase().getBytes(StandardCharsets.UTF_8));
    }
}