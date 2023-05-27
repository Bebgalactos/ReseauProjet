package user;

public class Subscriber{
    public void NewMessage(String channel, String message) {
        System.out.println("Nouveau message dans le channel [" + channel + "]: " + message);
    }
}
