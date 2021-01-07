import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class Broker {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pubs = context.socket(SocketType.XSUB);
        ZMQ.Socket subs = context.socket(SocketType.XPUB);
        subs.bind("tcp://*:"+args[0]);
        pubs.bind("tcp://*:"+args[1]);
        System.out.println("Porta para os publishers em: " + args[0]);
        System.out.println("Porta para os subscribers em: " + args[1]);
        System.out.println("*** O broker está ativo ***");
        ZMQ.proxy(pubs, subs, null);
    }
}
