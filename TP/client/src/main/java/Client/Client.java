package Client;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception{
        Socket s = new Socket("localhost",Integer.parseInt(args[0]));

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(SocketType.SUB);
        sub.connect("tcp://localhost:" + args[1]);

        Menu m = new Menu();
        Notifications notifications = new Notifications();

        ClientWriter cw = new ClientWriter(s,sub,notifications,m,6);
        ClientNotifier cr = new ClientNotifier(notifications,sub);

        new Thread(cw).start();
        new Thread(cr).start();
    }
}
