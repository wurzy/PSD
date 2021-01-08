package exchange;

import java.util.*;
import exchange.Protocol.*;


public class Company {

    private String company;
	private Queue<Order> buyOrders;
	private Queue<Order> sellOrders;
	private List<Transaction> transactions;

	public Company(String company) {
		this.company = company;
		this.buyOrders = new PriorityQueue<>(compareByPrice);
		this.sellOrders = new PriorityQueue<>(compareByPrice);
		this.transactions = new ArrayList<>();
	}

	public List<Transaction> registBuyOrder(Order buyOrder) {
		buyOrders.add(buyOrder);
		return newTransaction();
	}

	public List<Transaction> registSellOrder(Order sellOrder) {
		sellOrders.add(sellOrder);
		return newTransaction();
	}


	private List<Transaction> newTransaction() {

		List<Transaction> transactions = new ArrayList<>();
		boolean repeat = true;
		Order sellOrder;
		Order buyOrder;

		while(repeat) {
			sellOrder = sellOrders.poll();
			buyOrder = buyOrders.poll();

			if (isTransactionPossible(sellOrder, buyOrder)) {

				int quantity = Math.min(sellOrder.getQuantity(), buyOrder.getQuantity());

				float sellPrice = sellOrder.getPrice();
				float buyPrice = buyOrder.getPrice();
				float price = roundPrice((sellPrice + buyPrice) / 2);

				Transaction transaction = new Transaction(company, sellOrder.getUser(), buyOrder.getUser(), quantity, price);
				transactions.add(transaction);

				if (quantity < buyOrder.getQuantity()) {
					buyOrder.decreaseQuantity(quantity);
					buyOrders.add(buyOrder);
				} else if (quantity > buyOrder.getQuantity()) {
					sellOrder.decreaseQuantity(quantity);
					sellOrders.add(sellOrder);
				}

			} else {
				if (sellOrder != null) 
					sellOrders.add(sellOrder);
				if (buyOrder != null)
					buyOrders.add(buyOrder);
				repeat = false;
			}
		}
		return transactions;
	}


	private boolean isTransactionPossible(Order sellOrder, Order buyOrder) {
		return (sellOrder != null) && (buyOrder != null) && (sellOrder.getPrice() <= buyOrder.getPrice());
	}

	private float roundPrice(float price) {
		float rp = price * 100;
		long tmp = Math.round(rp);
		return (float) tmp / 100;
	}

	private Comparator<Order> compareByPrice = (Order o1, Order o2) -> Float.compare(o1.getPrice(), o2.getPrice());
}
