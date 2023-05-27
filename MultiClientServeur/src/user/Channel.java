package user;

import server.Client;
import server.SubscriberThread;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Channel {
    private String name;
    private Set<SubscriberThread> subscribers;

    public Channel(String name) {
        this.name = name;
        this.subscribers = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void subscribe(SubscriberThread client) {
        subscribers.add(client);
    }

    public void unsubscribe(Client client) {
        subscribers.remove(client);
    }

    public static Channel findChannelInList(List<Channel> channels, String channelName){
        for(Channel c : channels){
            if(c.getName().equals(channelName)){
                return c;
            }
        }
        return null;
    }

    public void publish(String message) throws IOException {
        for (SubscriberThread c : subscribers) {
            c.NewMessage(message);
        }
    }
}
