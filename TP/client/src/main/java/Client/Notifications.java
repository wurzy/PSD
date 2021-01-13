package Client;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class Notifications {
    private ArrayList<String> notifications;
    private TreeSet<String> subscriptions;
    private final List<String> districts;

    public Notifications(){
        this.notifications = new ArrayList<>();
        this.subscriptions = new TreeSet<>();
        this.districts = Arrays.asList
                ("Aveiro","Beja","Braga","Bragança","Castelo Branco",
                "Coimbra","Évora","Faro","Guarda","Leiria","Lisboa","Portalegre",
                "Porto","Santarém","Setúbal","Viana do Castelo","Vila Real","Viseu");
    }

    public synchronized void add(String s){
        notifications.add(s);
    }

    public synchronized void print(){
        notifications.forEach(System.out::println);
    }

    public synchronized boolean maybeAdd(String district){
        return subscriptions.add(district);
    }

    public synchronized boolean maybeRemove(String district){
        return subscriptions.remove(district);
    }

    public synchronized boolean canAdd(){
        return this.subscriptions.size() <= 3;
    }

    public synchronized void printSubscribed(){
        int i = 1;
        for(String s: subscriptions){
            System.out.println(i++ + " - " + s);
        }
    }

    public synchronized void printChoices(){
        System.out.println();
        System.out.println("Distritos disponíveis: ");
        int i = 1;
        for(String s: districts){
            System.out.println(i++ + " - " + s);
        }
    }
}
