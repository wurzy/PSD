package client;

import java.io.IOException;
import java.net.Socket;
import org.zeromq.ZMQ;


public class Client {
	
	public static void main(String[] args) throws Exception, IOException {
		int front_port = Integer.parseInt(args[1]);
		int sub_port = Integer.parseInt(args[2]);
		
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket sub = context.socket(ZMQ.SUB);
		sub.connect("tcp://localhost:" + args[2]);
		String s = "TEST";
		sub.subscribe(s);

		Socket cli = new Socket(args[0], front_port);
		ClientInfo info = new ClientInfo();
		Reader reader = new Reader(cli, info);
		Stub stub = new Stub(cli, info, sub);
		Notificator notifier = new Notificator(info, sub);

		reader.start();
		stub.start();
		notifier.start();
	}
}
