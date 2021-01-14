package District;

import Protos.MessageBuilder;
import Protos.Messages.*;
import org.zeromq.ZMQ;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Notifier implements Runnable{
    private ZMQ.Socket pub;
    private District district;

    private InputStream in;
    private OutputStream out;

    private ZMQ.Socket priv;

    public Notifier(ZMQ.Socket pub, ZMQ.Socket priv, Socket info, District district) throws Exception {
        this.pub = pub;
        this.district = district;
        this.in = info.getInputStream();
        this.out = info.getOutputStream();
        this.priv = priv;
    }

    public void run(){
        while(true){
            Message m = recvMessage();
            Type type = m.getType();
            try{
                if(type.equals(Type.LOCATION_PING)){
                    Location lp = m.getLocation();
                    locationPing(lp.getUsername() , new Point(lp.getCoordx(),lp.getCoordy()));
                }
                else if(type.equals(Type.SICK_PING)){
                    district.incrementInfected();
                    warnSick(m.getSickPing().getUsername());
                }
                else if (type.equals(Type.NR_PEOPLE)){
                    Location lp = m.getLocation();
                    nrPeople(new Point(lp.getCoordx(),lp.getCoordy()));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void publish(String msg){
        pub.send("[" + district.getName() + "] " + addDate(msg));
        System.out.println("Notificação pública enviada: " + msg);
    }

    private String addDate(String msg){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss '-' ");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date) + msg;
    }

    private void locationPing(String user, Point p){
        int current = district.incrementConcentration(p);
        int previous;
        if(!district.userExists(user)){
            district.addUser(user);
        }
        else {
            System.out.println("---------------------------------------------------------------------------------");
            Point last = district.getCurrentLocation(user);
            previous = district.decrementConcentration(last);
            publish(previous > 0
                    ? "Diminuição de concentração na localização " + p.toString() + " [TOTAL: " + previous + "]"
                    : "A localização " + last.toString() + " está vazia");
        }
        publish("Aumento de concentração na localização " + p.toString() + " [TOTAL: " + current + "]");
        district.addCoord(user,p);
    }

    private void warnSick(String user){
        String mega = district.getUsersToNotify(user);
        priv.send(MessageBuilder.notifyUsers(mega).toByteArray());
        publish("Alerta, foi detetado um utilizador infetado [TOTAL: " + district.getTotal() +"]");
    }

    private void nrPeople(Point p) throws Exception{
        int x = this.district.getCurrentConcentration(p);
        MessageBuilder.send(MessageBuilder.nrPeople(x),out);
    }

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
}