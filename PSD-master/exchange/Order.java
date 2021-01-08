package exchange;

public class Order {

    private String user;
    private String company;
    private int quantity;
    private float price;

    public Order(String user, String company, int quantity, float price) {
        this.user = user;
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }

    public String getUser() {
        return user;
    }

    public String getCompany() {
        return company;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }
    
}
