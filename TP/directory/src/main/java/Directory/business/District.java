package Directory.business;
import java.util.*;
import java.util.stream.Collectors;

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
    private String name;
    private HashMap<String,ArrayList<Point>> users;
    private HashMap<String,Boolean> sick;
    private HashMap<String,Integer> concentrationMax;
    private int total;
    private int infected;

    public District(String name){
        this.name = name;
        this.users = new HashMap<>();
        this.sick = new HashMap<>();
        this.concentrationMax = new HashMap<>();
        this.total = 0;
        this.infected = 0;
    }

    public String getName(){
        return this.name;
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

    public int getContactedInfected(){
        TreeSet<String> inf = new TreeSet<>();
        getInfectedUsers().forEach((infected,history) -> {
            getAllExcept(infected).forEach((key,value) -> {
                if(anyInCommon(history,value)){
                    inf.add(key);
                }
            });
        });
        return inf.size();
    }

    private HashMap<String, ArrayList<Point>> getInfectedUsers(){
        return this.sick.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(
                        Collectors.toMap(p -> p, this::getHistory, (left, right) -> right, HashMap::new)
                );
    }

    private ArrayList<Point> getHistory(String user){
        return this.users.get(user);
    }

    private boolean anyInCommon(ArrayList<Point> l1, ArrayList<Point> l2){
        for(Point p: l1){
            for(Point p2: l2){
                if(p2.equals(p))
                    return true;
            }
        }
        return false;
    }

    private HashMap<String,ArrayList<Point>> getAllExcept(String user){
        return this.users
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(user))
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (left, right) -> right, HashMap::new)
                );
    }

    public void setConcentrationMax(Point p, int x){
        if(this.concentrationMax.containsKey(p.toString()) && this.concentrationMax.get(p.toString()) > x){
            return;
        }
        this.concentrationMax.put(p.toString(),x);
    }

    public HashMap<String, Integer> getConcentrationMax() {
        return concentrationMax;
    }
}
