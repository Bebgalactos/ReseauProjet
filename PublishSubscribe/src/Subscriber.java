import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Subscriber{
    public void NewMessage(String channel, String message) {
        System.out.println("Nouveau message dans le channel [" + channel + "]: " + message);
    }
}




