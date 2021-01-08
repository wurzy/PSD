package client;

import client.Protocol.*;
import org.zeromq.ZMQ;
import client.Protocol.*;


public class Notificator extends Thread {
    private ClientInfo client;
    private ZMQ.Socket sub;

    public Notificator(ClientInfo clientInfo, ZMQ.Socket sub) {
        this.client = clientInfo;
        this.sub = sub;
    }

    public void run() {
        String msg;
        while(true) {
            byte[] b = sub.recv();
            msg = new String(b);
            client.addNotification(msg);
        }
    }

}
