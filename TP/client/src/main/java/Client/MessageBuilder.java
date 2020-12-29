package Client;

import Protos.Messages.*;

public class MessageBuilder {

    public static Message login(String user, String password){
        Login l = Login.newBuilder().setUsername(user).setPassword(password).build();
        return Message.newBuilder().setType(Type.LOGIN).setLoginData(l).build();
    }

    public static Message register(String user, String password, String district, int coordx, int coordy){
        Register r = Register.newBuilder().setUsername(user).setPassword(password).setDistrict(district).setCoordx(coordx).setCoordy(coordy).build();
        return Message.newBuilder().setType(Type.REGISTER).setRegisterData(r).build();
    }
}