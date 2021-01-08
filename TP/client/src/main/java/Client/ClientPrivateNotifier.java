package Client;

import Protos.MessageBuilder;
import Protos.Messages.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ClientPrivateNotifier implements Runnable{
    private Notifications notifications;
    private InputStream in;
    private Socket s;
    private volatile boolean run;

    public ClientPrivateNotifier(Notifications notifications, Socket s) throws Exception{
        this.notifications = notifications;
        this.in = s.getInputStream();
        this.s = s;
        this.run = true;
    }

    public void run() {
        while(run) {
            Message m = recvMsg();
            notifications.add(m.getNotification().getNotification());
        }
    }

    public void stop() throws Exception{
        run = false;
        s.close();
    }

    private Message recvMsg(){
        Message m = null;
        byte[] buf = new byte[4096], norm;
        int n;
        try{
            n = in.read(buf);
            norm = Arrays.copyOf(buf,n);
            m = Message.parseFrom(norm);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }
}
