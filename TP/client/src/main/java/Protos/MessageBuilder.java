package Protos;
import Protos.Messages.*;

import java.io.OutputStream;

public class MessageBuilder {

    public static void send(Message m, OutputStream out) throws Exception{
        out.write(m.toByteArray());
        out.flush();
    }

    public static Message login(String user, String password){
        Login li = Login.newBuilder().setUsername(user).setPassword(password).build();
        return Message.newBuilder().setType(Type.LOGIN).setLogin(li).build();
    }

    public static Message register(String user, String password, String district){
        Register r = Register.newBuilder().setUsername(user).setPassword(password).setDistrict(district).build();
        return Message.newBuilder().setType(Type.REGISTER).setRegister(r).build();
    }

    public static Message logout(){
        return Message.newBuilder().setType(Type.LOGOUT).build();
    }

    public static Message location(int coordx, int coordy){
        Location loc = Location.newBuilder().setCoordx(coordx).setCoordy(coordy).build();
        return Message.newBuilder().setType(Type.LOCATION).setLocation(loc).build();
    }

    public static Message sick(){
        return Message.newBuilder().setType(Type.SICK).build();
    }

    public static Message numberOfPeople(int coordx, int coordy) {
        Location loc = Location.newBuilder().setCoordx(coordx).setCoordy(coordy).build();
        return Message.newBuilder().setType(Type.NR_PEOPLE).setLocation(loc).build();
    }

    public static Message port(int port){
        PrivateNotificationsPort p = PrivateNotificationsPort.newBuilder().setPort(port).build();
        return Message.newBuilder().setType(Type.PORT).setPort(p).build();
    }
}