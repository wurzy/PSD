package Client;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception{
        Socket s = new Socket("localhost",12345);
        //Socket n = new Socket("localhost",12346);
        Menu m = new Menu();
        ClientWriter cw = new ClientWriter(s,m,10);
        //ClientReader cr = new ClientReader(s,m);
        new Thread(cw).start();
        //new Thread(cr).start();
    }
}
