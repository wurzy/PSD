package Client;

import Protos.Messages.*;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ClientPrivateNotifier implements Runnable{
    private ServerSocket ss;
    private Notifications notifications;
    private InputStream in;
    private Socket s;
    private volatile boolean run;

    public ClientPrivateNotifier(Notifications notifications, ServerSocket ss){
        this.notifications = notifications;
        this.ss = ss;
        this.run = true;
    }

    public void run() {
        try{
            this.s = ss.accept();
            this.in = s.getInputStream();
            while(run){
                Message m = recvMsg();
                notifications.add(m.getNotification().getNotification());
            }
        }
        catch(Exception ignore){}
    }

    public void stop() throws Exception{
        run = false;
        in.close();
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
        catch(Exception ignore){
        }
        return m;
    }
}
