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


    public District(String name){
        this.name = name;
        this.userCoords = new HashMap<>();
        this.userSick = new HashMap<>();
        this.concentration = new HashMap<>();
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

    public synchronized boolean userExists(String user){
        return this.userCoords.containsKey(user);
    }

    public synchronized Point getCurrentLocation(String user){
        ArrayList<Point> ps = this.userCoords.get(user);
        return ps.get(ps.size()-1);
    }

    // basta saber que incrementou
    public synchronized void incrementConcentration(Point p){
        String s = p.toString();
        if(this.concentration.containsKey(s)){
            int x = this.concentration.get(s);
            this.concentration.put(s,x+1);
        }
        else {
            this.concentration.put(s,1);
        }
    }

    // se esvaziou a localizaçao no distrito
    public synchronized boolean decrementConcentration(Point p){
        String s = p.toString();
        if(this.concentration.containsKey(s)){
            int x = this.concentration.get(s);
            this.concentration.put(s,x-1);
            return false;
        }
        else {
            this.concentration.put(s,0);
            return true;
        }
    }
}
