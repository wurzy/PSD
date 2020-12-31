package Client;

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

/**
 * Class that randomizes a path from a start position, using a N*N grid.
 * @author Válter Carvalho
 */
public class Randomizer implements Runnable{
    private int grid;
    private Point current;
    private Random rand;

    public Randomizer(Point start, int grid){
        this.grid = grid;
        this.current = start;
        this.rand = new Random();
    }

    public void run(){
        try {
            while(true) {
                Point p = next();
                System.out.println(p);
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
