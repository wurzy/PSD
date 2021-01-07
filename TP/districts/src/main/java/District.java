import org.zeromq.ZMQ;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

class Point {
    int x;
    int y;

    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(" + x + "," + y + ")";
    }
}

public class District{
    private String name;
    private HashMap<String, ArrayList<Point>> userCoords;
    private HashMap<String, Boolean> userSick;

    public District(String name){
        this.name = name;
        this.userCoords = new HashMap<>();
        this.userSick = new HashMap<>();
    }

    public String getName(){
        return name;
    }

    public synchronized void addUser(String user){
        this.userCoords.put(user,new ArrayList<>());
        this.userSick.put(user,false);
    }

    public synchronized void setSick(String user){
        this.userSick.put(user,true);
    }

    public synchronized void addCoord(String user, Point p){
        ArrayList<Point> novo = new ArrayList<>(this.userCoords.get(user));
        novo.add(p);
        this.userCoords.put(user,novo);
    }
}
