package exchange;
import exchange.Protocol.*;

public class MessageCreator {

    MessageCreator() {

    }

    public Message createSellerResponse(Transaction t) {
        Protocol.Order o = Protocol.Order.newBuilder().setCompany(t.getCompany()).setQuantity(t.getQuantity()).setPrice(t.getPrice()).build();
        Message msg = Message.newBuilder().setDest(t.getSeller()).setType("RESPONSE").setOrder(o).build();

        return msg;
    }

    public Message createBuyerResponse(Transaction t) {
        Protocol.Order o = Protocol.Order.newBuilder().setCompany(t.getCompany()).setQuantity(t.getQuantity()).setPrice(t.getPrice()).build();
        Message msg = Message.newBuilder().setDest(t.getBuyer()).setType("RESPONSE").setOrder(o).build();

        return msg;
    }

}