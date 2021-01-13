package District;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.net.ServerSocket;
import java.net.Socket;

public class DistrictServer {
    /**
     * District.DistrictServer class for the district server
     * @param args arg0 -> portPrivate, arg1 -> portPublic, arg2 -> broker , arg3 -> name
     * 12345 8001 8102 Braga
     */
    public static void main(String[] args) throws Exception{
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pubPublic = context.socket(SocketType.PUB);
        pubPublic.connect("tcp://localhost:" + args[1]); // connect to broker
        ServerSocket ss = new ServerSocket(Integer.parseInt(args[2]));
        Socket priNotif = ss.accept();
        //Socket priNotif = new Socket("localhost",Integer.parseInt(args[0]));
        //pubPrivate.connect("tcp://localhost:" + args[1]);
        District d = new District(args[3]);
        PublicNotifications pubNotif = new PublicNotifications(pubPublic,priNotif,d);
        new Thread(pubNotif).start();
    }
}
