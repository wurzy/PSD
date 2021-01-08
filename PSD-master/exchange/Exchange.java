package exchange;

import java.util.HashMap;
import java.util.Map;
import org.zeromq.ZMQ;
import java.util.List;
import java.util.ArrayList;

import exchange.Protocol.*;

public class Exchange {

	private Map<String, Company> companies;
	private ZMQ.Socket push;
	private ZMQ.Socket pull;
	private ZMQ.Socket pub;

	public static void main(String[] args) {

		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket push = context.socket(ZMQ.PUSH);
		push.connect("tcp://localhost:" + args[0]);

		ZMQ.Socket pull = context.socket(ZMQ.PULL);
		pull.bind("tcp://*:" + args[1]);

		ZMQ.Socket pub = context.socket(ZMQ.PUB);
		pub.connect("tcp://localhost:" + args[2]);

		Exchange exchange = populateExchange(push, pull, pub, Integer.parseInt(args[3]));
		DirectoryManager directory = new DirectoryManager();
		MessageCreator creator = new MessageCreator();
		List<Transaction> transactions;

		String pub_msg;

		while (true) {

			Message msg = exchange.recvMessage();
			Order order = exchange.createOrder(msg);
			String company = msg.getOrder().getCompany();

			if (msg.getType().equals("SELL"))
				transactions = exchange.addSellOrder(company, order);
			else
				transactions = exchange.addBuyOrder(company, order);

			for (Transaction t : transactions) {
				if(t != null) {
					try {
						directory.sendTransaction(t);
					} catch (Exception e) {
						System.out.println("Error sending data to Directory");
					}

					Message mSeller = creator.createSellerResponse(t);
					Message mBuyer = creator.createBuyerResponse(t);

					push.send(mSeller.toByteArray());
					push.send(mBuyer.toByteArray());
					pub_msg = t.getCompany() + ": " +  Integer.toString(t.getQuantity()) + " units of stock shares traded for " + Float.toString(t.getPrice());
					pub.send(pub_msg);
				}
			}
		}
	}



	public Exchange(ZMQ.Socket push , ZMQ.Socket pull, ZMQ.Socket pub) {
		this.companies = new HashMap<>();
		this.push = push;
		this.pull = pull;
		this.pub = pub;
	}

	public Exchange(ZMQ.Socket push , ZMQ.Socket pull, ZMQ.Socket pub, Map<String, Company> companies) {
		this.companies = companies;
		this.push = push;
		this.pull = pull;
		this.pub = pub;
	}

	public List<Transaction> addBuyOrder(String company, Order buyOrder) {
		return this.companies.get(company).registBuyOrder(buyOrder);
	}

	public List<Transaction> addSellOrder(String company, Order sellOrder) {
		return this.companies.get(company).registSellOrder(sellOrder);
	}

	public Order createOrder(Message m) {
		String user = m.getUser().getUsername();
		String company = m.getOrder().getCompany();
		int quantity = m.getOrder().getQuantity();
		float price = m.getOrder().getPrice();

		return new Order(user,company,quantity,price);
	}

	public Message recvMessage() {
		try {
			byte binary[] = pull.recv();
			Message msg = Message.parseFrom(binary);
			return msg;
		} catch (Exception e) {
			System.out.println("recvMsg() occur exception!" + e.toString());
		}
		return null;
	}


    public static Exchange populateExchange(ZMQ.Socket push , ZMQ.Socket pull, ZMQ.Socket pub, int number) {

        Map<String,Company> companies = new HashMap();

        switch(number){
            case 1: 
                companies.put("Gota", new Company("Gota")); 
                companies.put("Peões", new Company("Peões")); 
                companies.put("Speedy", new Company("Speedy")); 
			case 2:
                companies.put("Diobar", new Company("Diobar")); 
                companies.put("Carpe Noctem", new Company("Carpe Noctem")); 
                companies.put("Stephane", new Company("Stephane")); 
			case 3:
				companies.put("Bar Académico", new Company("Bar Académico"));
				companies.put("Pão de Forma", new Company("Pão de Forma"));
            default: break;
        }

        return new Exchange(push, pull, pub, companies);
    }

}
