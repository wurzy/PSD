package District;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DistrictServer {
    private static HashMap<String,Integer> atomToId = new HashMap<>();

    /**
     * District.DistrictServer class for the district server
     * @param args arg0 -> privateNotifications, arg1 -> broker, arg2 -> GetFrontendPings, arg3 -> atomName, arg4 -> directory port
     * Example: 12347 8001 8102 braga 8080
     */
    public static void main(String[] args) throws Exception{
        initMap();
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pubPublic = context.socket(SocketType.PUB);
        ZMQ.Socket pubPriv = context.socket(SocketType.PUSH);
        pubPublic.connect("tcp://localhost:" + args[1]); // connect to broker
        pubPriv.connect("tcp://localhost:" + args[0]); // connect to private notif socket on frontend
        ServerSocket ss = new ServerSocket(Integer.parseInt(args[2]));
        System.out.println("*** O servidor distrital de " + args[3] + " está ativo ***");
        Socket frontEndInfo = ss.accept();
        District d = new District(args[3],atomToId.get(args[3]));
        int directory = Integer.parseInt(args[4]);
        Notifier notif = new Notifier(pubPublic,pubPriv,frontEndInfo,d,directory);
        new Thread(notif).start();
    }

    private static void initMap(){
        atomToId.put("aveiro",1);
        atomToId.put("beja",2);
        atomToId.put("braga",3);
        atomToId.put("braganca",4);
        atomToId.put("castelo_branco",5);
        atomToId.put("coimbra",6);
        atomToId.put("evora",7);
        atomToId.put("faro",8);
        atomToId.put("guarda",9);
        atomToId.put("leiria",10);
        atomToId.put("lisboa",11);
        atomToId.put("portalegre",12);
        atomToId.put("porto",13);
        atomToId.put("santarem",14);
        atomToId.put("setubal",15);
        atomToId.put("viana_do_castelo",16);
        atomToId.put("vila_real",17);
        atomToId.put("viseu",18);
    }
}
