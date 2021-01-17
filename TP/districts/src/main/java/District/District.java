package District;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

class Point {
    int x;
    int y;

    Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean equals(Point p){
        return x == p.x && y == p.y;
    }

    public String toString(){
        return "(" + x + "," + y + ")";
    }
}

public class District{
    private final String name;
    private int id;
    private HashMap<String, ArrayList<Point>> userCoords;
    private HashMap<String, Integer> concentration;
    private TreeSet<String> sick;
    private int total;

    public District(String name, int id){
        this.name = name;
        this.userCoords = new HashMap<>();
        this.concentration = new HashMap<>();
        this.sick = new TreeSet<>();
        this.total = 0;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public synchronized void addUser(String user){
        this.userCoords.put(user,new ArrayList<>());
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

    public synchronized void sickUser(String user){
        Point p = getCurrentLocation(user);
        if (p!=null) {
            int x = this.concentration.get(p.toString());
            this.concentration.put(p.toString(),--x); 
        }
        //this.userCoords.remove(user);
        this.sick.add(user);
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

    public synchronized int getCurrentConcentration(Point p){
        if (this.concentration.containsKey(p.toString())){
            return this.concentration.get(p.toString());
        }
        return 0;
    }

    public synchronized int getTotal(){
        return total;
    }

    public synchronized void incrementInfected() {
        this.total++;
    }

    // Separated by commas
    public synchronized String getUsersToNotify(String user){
        ArrayList<String> users = usersNear(user);
        if (users == null) return null;
        users.removeIf(sick::contains);
        return String.join(",", users);
    }

    private synchronized ArrayList<String> usersNear(String user){
        Point p = this.getCurrentLocation(user);
        if (p == null) return null;
        ArrayList<String> ret = new ArrayList<>();
        for(Map.Entry<String,ArrayList<Point>> entry : this.userCoords.entrySet()){
            if (!entry.getKey().equals(user)){
                for(Point point : entry.getValue()){
                    if(p.equals(point)){
                        ret.add(entry.getKey());
                        break;
                    }
                }
            }
        }
        return ret;
    }
}
