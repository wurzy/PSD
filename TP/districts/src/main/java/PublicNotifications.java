import org.zeromq.ZMQ;

import java.text.SimpleDateFormat;
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

    private String addDate(String msg){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss '-' ");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date) + msg;
    }
}
