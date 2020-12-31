package Client;

public class Test {
    public static void main(String... args){
        Randomizer r = new Randomizer(new Point(1,1),10);
        new Thread(r).start();
    }
}
