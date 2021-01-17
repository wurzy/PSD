package District;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.net.ServerSocket;
import java.net.Socket;

public class DistrictServer {
    /**
     * District.DistrictServer class for the district server
     * @param args arg0 -> privateNotifications, arg1 -> broker, arg2 -> GetFrontendPings, arg3 -> name
     * Example: 12347 8001 8102 Braga
     */
    public static void main(String[] args) throws Exception{
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pubPublic = context.socket(SocketType.PUB);
        ZMQ.Socket pubPriv = context.socket(SocketType.PUSH);
        pubPublic.connect("tcp://localhost:" + args[1]); // connect to broker
        pubPriv.connect("tcp://localhost:" + args[0]); // connect to private notif socket on frontend
        ServerSocket ss = new ServerSocket(Integer.parseInt(args[2]));
        System.out.println("*** O servidor distrital de " + args[3] + " está ativo ***");
        Socket frontEndInfo = ss.accept();
        District d = new District(args[3]);
        Notifier notif = new Notifier(pubPublic,pubPriv,frontEndInfo,d);
        new Thread(notif).start();
    }
}
