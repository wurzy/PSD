package TestServer;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void main(String... args) throws Exception{
        ServerSocket ss = new ServerSocket(12345);
        while(true){
            Socket s = ss.accept();
            Handler h = new Handler(s);
        }
    }
}
