package directory.business;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    public int getContactedInfected(){

    }

    public int getContactedInfected2(){
        ArrayList<Point> novo = new ArrayList<>(Arrays.asList(new Point(1   ,2), new Point(2,0), new Point(2,2), new Point(0,0)));
        //ArrayList<Point> novo = new ArrayList<>();
        ArrayList<Point> novo2 = new ArrayList<>(Arrays.asList(new Point(1,1), new Point(0,0)));
        //ArrayList<Point> novo2 = new ArrayList<>();
        ArrayList<Point> novo3 = new ArrayList<>(Arrays.asList(new Point(2,0), new Point(3,2), new Point(1,2)));
        //ArrayList<Point> novo3 = new ArrayList<>();

        this.users.put("1",novo);
        this.users.put("2",novo2);
        this.users.put("3",novo3);

        this.sick.put("3",true);

        AtomicInteger total = new AtomicInteger();
        HashMap<String,Integer> coordsTotal = new HashMap<>();
        for(String s : this.users.keySet()){
            getUserLocationsByMap(s)
                    .forEach((key, value) -> {
                        if (coordsTotal.containsKey(key)){
                            int x = coordsTotal.get(key);
                            coordsTotal.put(key, ++x);
                        }
                        else {
                            coordsTotal.put(key, value);
                        }
                    });
        }
        getInfectedUsers().forEach(user -> {
            coordsTotal.forEach((key,value) -> {
                if(wasInLocation(user,key)){
                    total.getAndAdd(value - 1);
                }
            });
        });
        System.out.println(getInfectedUsers());
        System.out.println(coordsTotal);
        System.out.println(total.get());
        return total.get();
    }

    private HashMap<String,Integer> getUserLocationsByMap(String user){
        return this.users
                .get(user)
                .stream()
                .map(Point::toString)
                .distinct()
                .collect(
                        Collectors.toMap(p -> p,p -> 1, (left, right) -> right, HashMap::new)
                );
    }

    private ArrayList<String> getInfectedUsers(){
        return this.sick.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(
                        Collectors.toCollection(ArrayList::new)
                );
    }

    private boolean wasInLocation(String user, String p){
        return this.users.get(user)
                .stream()
                .anyMatch(p1 -> p1.toString().equals(p));
    }

    public Point getCurrentLocation(String user){
        ArrayList<Point> ps = this.users.get(user);
        if(ps.isEmpty()) return null;
        return ps.get(ps.size()-1);
    }
}
