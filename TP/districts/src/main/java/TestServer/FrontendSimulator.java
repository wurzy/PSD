package TestServer;

import Protos.Messages.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class FrontendSimulator {
    public static void main(String... args) throws Exception{
        byte[] buf;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pull = context.socket(SocketType.PULL);
        pull.bind("tcp://*:" + args[0]);
        while(true){
            buf = pull.recv();
            Message m = Message.parseFrom(buf);
            System.out.println("Mensagem: " + m);
        }
    }
}
