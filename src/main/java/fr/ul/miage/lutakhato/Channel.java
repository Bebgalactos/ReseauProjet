package fr.ul.miage.lutakhato;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Channel {
    private String name;

    private Set<ServerThread> servers;

    public Channel(String name) {
        this.name = name;
        this.servers = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void subscribe(ServerThread client) {
        servers.add(client);
    }

    public void unsubscribe(Client client) {
        servers.remove(client);
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
        for (ServerThread c : servers) {
            c.NewMessage(message);
        }
    }

    public Integer getNbSubscribers() {
        return servers.size();
    }
}
