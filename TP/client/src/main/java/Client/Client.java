package Client;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception{
        int privatePort = Integer.parseInt(args[1]);
        Socket s = new Socket("localhost",Integer.parseInt(args[0]));
        ServerSocket ss = new ServerSocket(privatePort);

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket sub = context.socket(SocketType.SUB);
        sub.connect("tcp://localhost:" + args[2]);

        Menu m = new Menu();
        Notifications notifications = new Notifications();

        ClientWriter cw = new ClientWriter(s,ss,sub,notifications,m,1,privatePort);
        ClientNotifier cr = new ClientNotifier(notifications,sub);

        new Thread(cw).start();
        new Thread(cr).start();
    }
}
