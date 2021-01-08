package District;

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
    private final String name;
    private HashMap<String, ArrayList<Point>> userCoords;
    private HashMap<String, Boolean> userSick;
    private HashMap<String, Integer> concentration;
    private int total;

    public District(String name){
        this.name = name;
        this.userCoords = new HashMap<>();
        this.userSick = new HashMap<>();
        this.concentration = new HashMap<>();
        this.total = 0;
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
        total++;
    }

    public synchronized void addCoord(String user, Point p){
        this.userCoords.get(user).add(p);
    }

    public synchronized boolean userExists(String user){
        return this.userCoords.containsKey(user);
    }

    public synchronized Point getCurrentLocation(String user){
        ArrayList<Point> ps = this.userCoords.get(user);
        if(ps.isEmpty()) return null;
        return ps.get(ps.size()-1);
    }

    // basta saber que incrementou
    public synchronized int incrementConcentration(Point p){
        String s = p.toString();
        int x = 1;
        if(this.concentration.containsKey(s)){
            x = this.concentration.get(s);
            this.concentration.put(s,++x);
        }
        else {
            this.concentration.put(s,x);
        }
        return x;
    }

    // se esvaziou a localizaçao no distrito
    public synchronized int decrementConcentration(Point p){
        String s = p.toString();
        int x = 0;
        if(this.concentration.containsKey(s)){
            x = this.concentration.get(s);
            this.concentration.put(s,--x);
        }
        else {
            this.concentration.put(s,x);
        }
        return x;
    }

    public synchronized int getTotal(){
        return total;
    }
}
