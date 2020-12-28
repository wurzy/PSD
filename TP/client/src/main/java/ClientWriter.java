import java.awt.*;
import java.net.Socket;

public class ClientWriter implements Runnable{
    private Menu menu;
    private String user, password, district;
    private int coordx,coordy;
    private Socket s;

    public ClientWriter(Socket s, Menu menu){
        this.menu = menu;
        this.s = s;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDistrict() {
        return district;
    }

    public int getCoordX() {
        return coordx;
    }

    public int getCoordY() {
        return coordy;
    }

    public Socket getSocket() {
        return s;
    }

    public void initState(String user, String password, String district){
        this.user = user;
        this.password = password;
        this.district = district;
    }

    public void setLocation(int x, int y) {
        this.coordx = x;
        this.coordy = y;
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
                    ;
                else if (choice == 2)
                    ;
                else if (choice == 3)
                    ;
                break;
            default:
                System.out.println("Erro no parsing...");
                break;
        }
    }

    private void login(){
        System.out.println("Login");
    }

    private void register() {
        this.user = menu.readString("Nome de Utilizador: ");
        this.password = menu.readString("Palavra-passe: ");
        this.district = menu.readString("Distrito: ");
        this.coordx = menu.readInt("Coordenada X: ");
        this.coordy = menu.readInt("Coordenada Y: ");

        System.out.println(user + ", " + password + ", " + district + ": (" + coordx + "," + coordy + ")");
    }

    private void logout(){
        menu.setState(Menu.State.NOTLOGGED);
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
}
