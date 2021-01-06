package Client;
import Protos.MessageBuilder;
import Protos.Messages.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class ClientWriter implements Runnable{
    private int grid;
    private Thread locationPing;
    private Menu menu;
    private Socket s;
    private OutputStream out;
    private InputStream in;

    public ClientWriter(Socket s, Menu menu, int grid) throws Exception{
        this.menu = menu;
        this.s = s;
        this.out = s.getOutputStream();
        this.in = s.getInputStream();
        this.grid = grid;
        Random r = new Random();
        this.locationPing = new Thread(new Randomizer(new Point(r.nextInt(grid),r.nextInt(grid)),grid)); // random start position on a N*N grid
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
                    location();
                else if (choice == 3)
                    numberOfPeople();
                break;
            default:
                System.out.println("Erro no parsing/estado...");
                break;
        }
    }

    private void login() throws Exception{
        String user = menu.readString("Nome de Utilizador: ");
        String password = menu.readString("Palavra-passe: ");
        MessageBuilder.send(MessageBuilder.login(user,password),out);

        Message rep = getReply();
        System.out.println(rep.getReply().getMessage());

        menu.setState(rep.getReply().getResult() ? Menu.State.LOGGED : Menu.State.NOTLOGGED);
        menu.show();
        locationPing.start();
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
        MessageBuilder.send(MessageBuilder.logout(),out);

        Message rep = getReply();
        System.out.println(rep.getReply().getMessage());

        menu.setState(Menu.State.NOTLOGGED);
        menu.show();
    }

    private void sick() throws Exception{
        MessageBuilder.send(MessageBuilder.sick(),out);

        Message rep = getReply();
        System.out.println(rep.getReply().getMessage());

        menu.setState(Menu.State.NOTLOGGED);
        menu.show();
    }

    private void location() throws Exception{
        int coordx = menu.readInt("Coordenada X: ");
        int coordy = menu.readInt("Coordenada Y: ");

        MessageBuilder.send(MessageBuilder.location(coordx,coordy),out);

        Message rep = getReply();
        System.out.println(rep.getReply().getMessage());

        menu.setState(Menu.State.LOGGED);
        menu.show();
    }

    private void numberOfPeople() throws Exception{

        menu.setState(Menu.State.LOGGED);
        menu.show();
    }

    private void leave(){
        System.out.println("A sair...");
        try {
            s.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void confirm(Message rep){
        System.out.println("*** " + rep.getReply().getMessage() + " ***");
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
