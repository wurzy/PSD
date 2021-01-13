package Client;
import Protos.MessageBuilder;
import Protos.Messages.*;
import org.zeromq.ZMQ;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class ClientWriter implements Runnable{
    private int grid;
    private Random rand;
    private Thread locationPing;

    private Menu menu;
    private Notifications notif;
    private ZMQ.Socket sub;
    private int port; // for the private notifications
    private ClientPrivateNotifier cpn;

    private Socket s;
    private OutputStream out;
    private InputStream in;

    public ClientWriter(Socket s, ZMQ.Socket sub, Notifications notif, Menu menu, int grid, int port, ClientPrivateNotifier cpn) throws Exception{
        this.menu = menu;
        this.s = s;
        this.sub = sub;
        this.notif = notif;
        this.out = s.getOutputStream();
        this.in = s.getInputStream();
        this.grid = grid;
        this.port = port;
        this.cpn = cpn;
        this.rand = new Random();
    }

    public void run() {
        int choice;
        menu.show();
        while (((choice = menu.choice()) != -1)) {
            try {
                parse(choice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parse(Integer choice) throws Exception {
        switch (menu.getState()) {
            case NOTLOGGED:
                if (choice == 0)
                    leave();
                else if (choice == 1)
                    login();
                else if (choice == 2)
                    register();
                break;
            case LOGGED:
                if (choice == 0)
                    logout();
                else if (choice == 1)
                    sick();
                else if (choice == 2)
                    numberOfPeople();
                else if (choice == 3)
                    notifications();
                else if (choice == 4)
                    subscribe();
                else if (choice == 5)
                    unsubscribe();
                break;
            default:
                System.out.println("Erro no parsing/estado...");
                break;
        }
    }

    private void login() throws Exception{
        String user = menu.readString("Nome de Utilizador: ");
        String password = menu.readString("Palavra-passe: ");
        System.out.println(MessageBuilder.login(user,password));
        MessageBuilder.send(MessageBuilder.login(user,password),out);

        Message rep = getReply();
        confirm(rep);

        if(rep.getReply().getResult()){
            menu.setState(Menu.State.LOGGED);
            this.locationPing = new Thread(new Randomizer(new Point(rand.nextInt(grid),rand.nextInt(grid)),grid,out)); // random start position on a N*N grid
            MessageBuilder.send(MessageBuilder.port(port),out);
            locationPing.start();
        }
        else {
            menu.setState(Menu.State.NOTLOGGED);
        }
        menu.show();
    }

    private void register() throws Exception{
        String user = menu.readString("Nome de Utilizador: ");
        String password = menu.readString("Palavra-passe: ");
        String district = menu.readString("Distrito: ");
        MessageBuilder.send(MessageBuilder.register(user,password,district),out);

        Message rep = getReply();
        confirm(rep);

        menu.setState(Menu.State.NOTLOGGED);
        menu.show();
    }

    private void logout() throws Exception{
        locationPing.interrupt();
        MessageBuilder.send(MessageBuilder.logout(),out);

        Message rep = getReply();
        confirm(rep);

        menu.setState(Menu.State.NOTLOGGED);
        menu.show();
    }

    private void sick() throws Exception{
        MessageBuilder.send(MessageBuilder.sick(),out);

        Message rep = getReply();
        confirm(rep);

        menu.setState(Menu.State.NOTLOGGED);
        menu.show();
    }

    /*
    private void location() throws Exception{
        int coordx = menu.readInt("Coordenada X: ");
        int coordy = menu.readInt("Coordenada Y: ");

        MessageBuilder.send(MessageBuilder.location(coordx,coordy),out);

        Message rep = getReply();
        System.out.println(rep.getReply().getMessage());

        menu.setState(Menu.State.LOGGED);
        menu.show();
    }
*/
    private void numberOfPeople() throws Exception{
        int coordx = menu.readInt("Coordenada X: ");
        int coordy = menu.readInt("Coordenada Y: ");

        MessageBuilder.send(MessageBuilder.numberOfPeople(coordx,coordy),out);

        Message rep = getReply();
        confirm(rep);

        menu.setState(Menu.State.LOGGED);
        menu.show();
    }

    private void notifications(){
        this.notif.print();

        softConfirm();
        menu.setState(Menu.State.LOGGED);
        menu.show();
    }

    private void subscribe(){
        if(notif.canAdd()){
            String dist = menu.readString("Insira o distrito que pretende subscrever: ");
            if(notif.maybeAdd(dist)) {
                System.out.println("Adicionado o distrito " + dist + " às subscrições.");
                sub.subscribe("[" + dist + "]");
            }
            else {
                System.out.println("O distrito " + dist + " já está subscrito.");
            }
        }
        else {
            System.out.println("Não é possível subscrever a mais que 3 distritos.");
        }
        softConfirm();
        menu.setState(Menu.State.LOGGED);
        menu.show();
    }

    private void unsubscribe(){
        System.out.println("Distritos atuais: ");
        notif.printOut();
        String dist = menu.readString("Insira o distrito que pretende remover: ");
        if(notif.maybeRemove(dist)){
            System.out.println("Removido o distrito " + dist + " das subscrições.");
            sub.unsubscribe("[" + dist + "]");
        }
        else {
            System.out.println("O distrito " + dist + " não está subscrito.");
        }
        softConfirm();
        menu.setState(Menu.State.LOGGED);
        menu.show();
    }

    private void leave(){
        System.out.println("A sair...");
        if(!locationPing.isInterrupted()) locationPing.interrupt();
        try {
            s.close();
            cpn.stop();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void confirm(Message rep){
        System.out.println("*** " + rep.getReply().getMessage() + " ***");
        menu.readString("Premir Enter para continuar ");
    }

    private void softConfirm(){
        menu.readString("Premir Enter para continuar ");
    }

    private Message getReply(){
        Message m = null;
        byte[] buf = new byte[4096], norm;
        int n;
        try{
            n = in.read(buf);
            norm = Arrays.copyOf(buf,n);
            m = Message.parseFrom(norm);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }
}
