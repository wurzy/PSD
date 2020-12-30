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
        try{
            while((m = Message.parseDelimitedFrom(in))!=null){
                System.out.println("Recebi uma mensagem: " + m);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
