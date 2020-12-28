import java.net.Socket;

public class ClientReader implements Runnable{
    private Socket s;
    private Menu menu;

    public ClientReader(Socket s, Menu menu){
        this.s = s;
        this.menu = menu;
    }

    public void run(){
        System.out.println("start reader");
        System.out.println("done reader");
    }
}
