package exchange;

public class Transaction {

	private String company;
	private String seller;
	private String buyer;
	private int quantity;
	private float price;

	public Transaction(String company, String seller, String buyer, int quantity, float price) {
		this.company = company;
		this.seller = seller;
		this.buyer = buyer;
		this.quantity = quantity;
		this.price = price;
	}

	public String getCompany() {
		return company;
	}

	public String getSeller() {
		return seller;
	}

	public String getBuyer() {
		return buyer;
	}

	public int getQuantity() {
		return quantity;
	}

	public float getPrice() {
		return price;
	}


}
