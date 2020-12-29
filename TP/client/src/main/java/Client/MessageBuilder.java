package Client;

import Protos.Messages.*;

public class MessageBuilder {

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
}