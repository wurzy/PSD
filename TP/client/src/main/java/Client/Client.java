package Client;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception{
        //int port = Integer.parseInt(args[0]);
        //Socket s = new Socket("127.0.0.1",port);
        Menu m = new Menu();
        ClientWriter cw = new ClientWriter(null,m);
        ClientReader cr = new ClientReader(null,m);
        new Thread(cw).start();
    }
}
