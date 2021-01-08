package District;

import org.zeromq.ZMQ;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class PublicNotifications implements Runnable{
    private ZMQ.Socket pub;
    private District district;

    public PublicNotifications(ZMQ.Socket pub, District district) {
        this.pub = pub;
        this.district = district;
    }

    public void run(){
        while(true){
            //receber msg
            //Message m = recvMessage();
            //enviar msg
            pub.send("[" + district.getName() + "] " + addDate("Enviei uma mensagem"));
            System.out.println("[" + district.getName() + "] " + addDate("Enviei uma mensagem"));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void publish(String msg){
        System.out.println("Notificação pública: " + msg);
        pub.send("[" + district.getName() + "] " + addDate(msg));
    }

    private String addDate(String msg){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss '-' ");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date) + msg;
    }

    private void locationPing(String user, Point p){
        int current = district.incrementConcentration(p);
        int dec, decprev;
        int previous = current - 1;
        Point last = district.getCurrentLocation(user);
        if(!district.userExists(user)){
            district.addUser(user);
        }
        district.addCoord(user,p);
        publish("Aumento de concentração na localização " + p.toString() + ", " + previous + " -> " + current);
        if(last!=null) {
            dec = district.decrementConcentration(last);
            decprev = dec + 1;
            if(dec > 0) {
                publish("Diminuição de concentração na localização " + p.toString() + ", " + decprev + " -> " + dec);
            }
            else {
                publish("A localização " + p.toString() + " está vazia");
            }
        }
    }

    private void warnSick(String user){
        district.setSick(user);
        publish("Alerta, foi detetado um utilizador infetado, total: " + district.getTotal());
    }

    /*
    private Message recvMessage(){
        Message m = null;
        byte[] buf = new byte[4096], norm;
        int n;
        try{
            n = in.read(buf);
            norm = Arrays.copyOf(buf,n);
            m = Message.parseFrom(norm);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }
    */
}
