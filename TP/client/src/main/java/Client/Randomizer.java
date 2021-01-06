package Client;

import Protos.MessageBuilder;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

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

public class Randomizer implements Runnable{
    private int grid;
    private Point current;
    private Random rand;
    private OutputStream out;

    public Randomizer(Point start, int grid, OutputStream out){
        this.grid = grid;
        this.current = start;
        this.rand = new Random();
        this.out = out;
    }

    public void run(){
        try {
            while(true) {
                Point p = next();
                MessageBuilder.send(MessageBuilder.location(p.x,p.y),out);
                Thread.sleep(5000);
            }
        }
        catch (InterruptedException ignore){}
        catch (Exception e) {e.printStackTrace();}
    }

    private Point next(){
        ArrayList<Point> gen = adjacent(this.current);
        Point p = gen.get(this.rand.nextInt(gen.size()));
        this.current = p;
        return p;
    }

    private ArrayList<Point> adjacent(Point p){
        ArrayList<Point> list = new ArrayList<>();

        list.add(new Point(p.x + 1, p.y));
        list.add(new Point(p.x - 1, p.y));
        list.add(new Point(p.x, p.y + 1));
        list.add(new Point(p.x, p.y - 1));

        list.removeIf(point -> !valid(point));
        return list;
    }

    private boolean valid(Point p){
        return (p.x <= this.grid && p.y <= this.grid && p.x >= 0 && p.y >= 0);
    }
}
