package fr.ul.miage.lutakhato;

import java.util.HashMap;
import java.util.Map;

public class Publisher {
    private Map<Channel, String> channels;
    public Publisher() {
        this.channels = new HashMap<>();
    }

    public Map<Channel, String> getChannels() {
        return channels;
    }

    public void setChannels(Map<Channel, String> channels) {
        this.channels = channels;
    }

    public void createChannel(String name) {
        channels.put(new Channel(name),name);
    }

    public void deleteChannel(String name) {
        channels.remove(name);
    }

}
