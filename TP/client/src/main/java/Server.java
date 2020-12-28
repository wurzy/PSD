import java.net.ServerSocket;
import java.net.Socket;

class Handler implements Runnable{

    public Handler(Socket s){

    }

    public void run(){

    }
}

public class Server {

    public void main(String... args) throws Exception{
        ServerSocket ss = new ServerSocket(12345);
        while(true){
            Socket s = ss.accept();
            Handler h = new Handler();
        }
    }
}
