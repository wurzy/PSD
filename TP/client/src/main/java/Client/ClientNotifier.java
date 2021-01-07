package Client;
import org.zeromq.ZMQ;

public class ClientNotifier implements Runnable{
    private Notifications notifications;
    private ZMQ.Socket sub;

    public ClientNotifier(Notifications notifications, ZMQ.Socket sub) {
        this.notifications = notifications;
        this.sub = sub;
    }

    public void run() {
        while(true) {
            notifications.add(new String(sub.recv()));
        }
    }
}
