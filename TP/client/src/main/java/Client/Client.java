package Client;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception{
        Socket s = new Socket("localhost",Integer.parseInt(args[0]));
        Socket privNotif = new Socket("localhost",Integer.parseInt(args[0]));

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(SocketType.SUB);
        sub.connect("tcp://localhost:" + args[2]);

        Menu m = new Menu();
        Notifications notifications = new Notifications();

        ClientPrivateNotifier cp = new ClientPrivateNotifier(notifications,privNotif);
        ClientWriter cw = new ClientWriter(s,sub,notifications,m,1,privNotif.getLocalPort(),cp);
        ClientNotifier cr = new ClientNotifier(notifications,sub);

        new Thread(cw).start();
        new Thread(cr).start();
        new Thread(cp).start();
    }
}
