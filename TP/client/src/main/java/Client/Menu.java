package Client;

import java.util.Scanner;

public class Menu {
    public enum State {
        NOTLOGGED,
        LOGGED
    }
    private Scanner input;
    private State state;

    public Menu(){
        this.state = State.NOTLOGGED;
        this.input = new Scanner(System.in);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void show() {
        switch (state) {
            case NOTLOGGED:
                System.out.println("+----------------- MENU INICIAL -----------------+\n" +
                        "| 1 - Log-in                                     |\n" +
                        "| 2 - Registar                                   |\n" +
                        "| 0 - Sair                                       |\n" +
                        "+------------------------------------------------+\n");
                break;
            case LOGGED:
                System.out.println("+----------------- MENU CLIENTE ------------------+\n" +
                        "| 1 - Comunicar Infeção                           |\n" +
                        "| 2 - Nº Pessoas numa Localização                 |\n" +
                        "| 3 - Ler Notificações                            |\n" +
                        "| 4 - Subscrever Distrito                         |\n" +
                        "| 5 - Anular Subscrição                           |\n" +
                        "| 0 - Logout                                      |\n" +
                        "+ ------------------------------------------------+\n");
                break;
        }
        System.out.print("Opção: ");
    }

    public Integer choice() {
        int choice;
        while((choice = choiceAux()) == -1) {
            System.out.println("Insira opção válida!");
            System.out.print("Opção: ");
        }
        return choice;
    }

    private Integer choiceAux() {
        int choice;
        try {
            choice = Integer.parseInt(input.nextLine());
            switch(state){
                case NOTLOGGED:
                    while(choice<0 || choice >2) {
                        System.out.println("Insira opção válida!");
                        System.out.print("Opção: ");
                        choice = Integer.parseInt(input.nextLine());
                    }
                    break;
                case LOGGED:
                    while(choice<0 || choice >5){
                        System.out.println("Insira opção válida!");
                        System.out.print("Opção: ");
                        choice = Integer.parseInt(input.nextLine());
                    }
                    break;
                default:
                    System.out.println("Erro no input...");
            }
        }
        catch(NumberFormatException e) {
            choice = -1;
        }
        return choice;
    }

    public String readString(String req) {
        System.out.println(req);
        return input.nextLine();
    }

    public String readNotEmptyString(String req){
        System.out.println(req);
        String s;
        while((s = input.nextLine()).equals("")){
            System.out.println(req);
        }
        return s;
    }

    public int readInt(String req){
        System.out.println(req);
        int choice;
        while((choice = readIntAux()) == -1) {
            System.out.println("Insira um inteiro válido!");
        }
        return choice;
    }

    private int readIntAux() {
        int res;
        try {
            res = Integer.parseInt(input.nextLine());
        }
        catch(NumberFormatException e) {
            res = -1;
        }
        return res;
    }
}
