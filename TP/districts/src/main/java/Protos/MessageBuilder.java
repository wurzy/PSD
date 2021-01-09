package Protos;
import Protos.Messages.*;
import java.io.OutputStream;

public class MessageBuilder {

    public static void send(Message m, OutputStream out) throws Exception{
        out.write(m.toByteArray());
        out.flush();
    }

    public static Message nrPeople(String user, int total){
        NrPeopleReply nr = NrPeopleReply.newBuilder().setUsername(user).setTotal(total).build();
        return Message.newBuilder().setType(Type.NR_PEOPLE_REPLY).setNrPeopleReply(nr).build();
    }

    public static Message notifyUsers(String users){
        NotifyUsers nu = NotifyUsers.newBuilder().setUsers(users).build();
        return Message.newBuilder().setType(Type.NOTIFY_USERS).setNotifyUsers(nu).build();
    }
}
