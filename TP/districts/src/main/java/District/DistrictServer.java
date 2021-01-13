package District;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import java.net.Socket;

public class DistrictServer {
    /**
     * District.DistrictServer class for the district server
     * @param args arg0 -> portPrivate, arg1 -> portPublic, arg2 -> broker , arg3 -> name
     */
    public static void main(String[] args) throws Exception{
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pubPublic = context.socket(SocketType.PUB);
        Socket priNotif = new Socket("localhost",Integer.parseInt(args[0]));
        pubPublic.connect("tcp://localhost:" + args[2]); // connect to broker
        //pubPrivate.connect("tcp://localhost:" + args[1]);
        District d = new District(args[3]);
        PublicNotifications pubNotif = new PublicNotifications(pubPublic,priNotif,d);
        new Thread(pubNotif).start();
    }
}