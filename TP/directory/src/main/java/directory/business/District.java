package directory.business;
import java.util.*;
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

public class District {
    private HashMap<String,ArrayList<Point>> users;
    private HashMap<String,Boolean> sick;
    private int total;
    private int infected;

    public District(){
        this.users = new HashMap<>();
        this.sick = new HashMap<>();
        this.total = 0;
        this.infected = 0;
    }

    public int getNumberOfUsers(){
        return this.users.size();
    }

    public int getNumberOfInfected(){
        return this.infected;
    }

    public void incrementInfected(){
        this.infected++;
    }

    public void sick(String user){
        this.sick.put(user,true);
        incrementInfected();
    }

    public void addUser(String user){
        this.total++;
        this.sick.put(user,false);
        this.users.put(user,new ArrayList<>());
    }

    public void removeUser(String user){
        this.users.remove(user);
    }

    public double getRatio(){
        return (double) infected/total;
    }

    public void addCoord(String user, Point p){
        this.users.get(user).add(p);
    }

    public boolean userExists(String user){
        return this.users.containsKey(user);
    }

    public Point getCurrentLocation(String user){
        ArrayList<Point> ps = this.users.get(user);
        if(ps.isEmpty()) return null;
        return ps.get(ps.size()-1);
    }
}
