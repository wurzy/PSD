package TestServer;

import Protos.MessageBuilder;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class PushSimulator {
    public static void main(String... args){
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket push = context.socket(SocketType.PUSH);
        push.connect("tcp://*:" + args[0]);
        push.send(MessageBuilder.nrPeople(10).toByteArray());
    }
}
