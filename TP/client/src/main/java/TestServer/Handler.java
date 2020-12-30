package TestServer;

import Protos.Messages.*;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {
    Socket s;
    InputStream in;
    OutputStream out;

    public Handler(Socket s) throws Exception{
        this.s = s;
        in = s.getInputStream();
        out = s.getOutputStream();
    }

    public void run(){
        Message m;
        try{
            while((m = Message.parseDelimitedFrom(in))!=null){
                System.out.println("Recebi uma mensagem: " + m);
                m.writeDelimitedTo(out);
                out.flush();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
