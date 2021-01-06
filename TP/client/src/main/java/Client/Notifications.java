package Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Notifications {
    private ArrayList<String> notifications;

    public synchronized void add(String s){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd',' HH:mm:ss '-' ");
        Date date = new Date(System.currentTimeMillis());
        String notification = formatter.format(date) + s;
        notifications.add(notification);
    }

    public synchronized void print(){
        notifications.forEach(System.out::println);
    }
}
