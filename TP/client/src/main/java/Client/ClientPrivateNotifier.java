package Client;

import Protos.Messages.*;

import javax.sound.midi.Soundbank;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;

public class ClientPrivateNotifier implements Runnable{
    private ServerSocket ss;
    private Notifications notifications;
    private InputStream in;
    private Socket s;
    private volatile boolean run;
    private boolean menuState;
    private int waitingNotifs;

    public ClientPrivateNotifier(Notifications notifications, ServerSocket ss){
        this.notifications = notifications;
        this.ss = ss;
        this.run = true;
        this.waitingNotifs = 0;
    }

    public void updateMenuState(boolean state) {
        this.menuState = state;
    }

    public int getWaitingNotifs() {
        return this.waitingNotifs;
    }

    public void resetWaitingNotifs() {
        this.waitingNotifs = 0;
    }

    public void run() {
        try{
            this.s = ss.accept();
            this.in = s.getInputStream();
            while(run){
                Message m = recvMsg();
                notifications.add(m.getNotification().getNotification());

                if (menuState){
                    System.out.print("\n\n*****************************************************\n" +
                            m.getNotification().getNotification() +
                            "\n*****************************************************");
                    System.out.print("\n\nOpção: ");
                }
                else{
                    this.waitingNotifs += 1;
                }
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
