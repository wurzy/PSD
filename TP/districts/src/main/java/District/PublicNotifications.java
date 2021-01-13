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

public class PublicNotifications implements Runnable{
    private ZMQ.Socket pub;
    private District district;

    private InputStream in;
    private OutputStream out;

    public PublicNotifications(ZMQ.Socket pub, Socket priv, District district) throws Exception {
        this.pub = pub;
        this.district = district;
        this.in = priv.getInputStream();
        this.out = priv.getOutputStream();
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
        System.out.println("Enviando notificação pública: " + msg);
        pub.send("[" + district.getName() + "] " + addDate(msg));
    }

    private String addDate(String msg){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss '-' ");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date) + msg;
    }

    private void locationPing(String user, Point p){
        //int current = district.incrementConcentration(p);
        //int dec, decprev;
        //int previous = current - 1;
        if(!district.userExists(user)){
            district.addUser(user);
        }
        //Point last = district.getCurrentLocation(user);
       // if(last != null) {
            //publish("Aumento de concentração na localização " + p.toString() + ", " + previous + " -> " + current);
        //}
        district.addCoord(user,p);
        //if(last!=null) {
           // dec = district.decrementConcentration(last);
           // decprev = dec + 1;
           // if(dec > 0) {
           //     publish("Diminuição de concentração na localização " + p.toString() + ", " + decprev + " -> " + dec);
           // }
           // else {
           //     publish("A localização " + p.toString() + " está vazia");
           // }
        //}
    }

    private void warnSick(String user) throws Exception{
        district.setSick(user);
        String mega = district.getUsersToNotify(user);
        MessageBuilder.send(MessageBuilder.notifyUsers(mega),out);
        publish("Alerta, foi detetado um utilizador infetado, total: " + district.getTotal());
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
            System.out.println("recebi msg: " + m);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }
}
