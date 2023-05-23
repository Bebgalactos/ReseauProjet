import java.util.Set;
import java.util.HashSet;

public class Channel {
        private String name;
        private Set<Subscriber> subscribers;

        public Channel(String name) {
            this.name = name;
            this.subscribers = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public void subscribe(Subscriber client) {
            subscribers.add(client);
        }

        public void unsubscribe(Subscriber client) {
            subscribers.remove(client);
        }

        public void publish(String message) {
            for (Subscriber subscriber : subscribers) {
                subscriber.NewMessage(name, message);
            }
        }
    }



