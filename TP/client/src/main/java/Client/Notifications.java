package Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

public class Notifications {
    private ArrayList<String> notifications;
    private TreeSet<String> subscriptions;

    public Notifications(){
        this.notifications = new ArrayList<>();
        this.subscriptions = new TreeSet<>();
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

}
