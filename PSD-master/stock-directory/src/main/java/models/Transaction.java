package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
    private final String company;
    private final int quantity;
    private final float price;

    @JsonCreator
    public Transaction(@JsonProperty("company") String company, @JsonProperty("quantity") int quantity, @JsonProperty("price") float price) {
        this.company = company;
        this.quantity = quantity;
        this.price = price;
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
}
