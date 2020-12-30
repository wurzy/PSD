package Client;

import Protos.Messages.*;

import java.io.InputStream;
import java.net.Socket;

public class ClientReader implements Runnable{
    private Socket s;
    private InputStream in;
    private Menu menu;

    public ClientReader(Socket s, Menu menu) throws Exception{
        this.s = s;
        this.menu = menu;
        in = s.getInputStream();
    }

    public void run(){
        Message m;
        byte[] buf = new byte[4096], norm;
        int n;
        try{
            while((n=in.read(buf))>0){
                norm = normalizeMessage(buf,n);
                m = Message.parseFrom(norm);
                System.out.println(m);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private byte[] normalizeMessage(byte[] buf, int n){
        byte[] norm = new byte[n];

        for(int i = 0; i < n; i++){
            norm[i] = buf[i];
        }

        return norm;
    }
}
