package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Optional;

import java.util.Random;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Company {
    private String name;
    private Exchange exchange;
    private Stock yesterday, today;

    public Company(String name, Exchange exchange) {
        this.name = name;

        Optional<Float> initialValue = generateInitialStocks();
        yesterday = new Stock(initialValue);
        yesterday.close();
        today = yesterday.open();

        this.exchange = exchange;
    }

    public String getName() {
        return name;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public Stock getYesterday() {
        if (yesterday.hasTransactions())
            return yesterday;

        return null;
    }

    public Stock getToday() {
        if (today.hasTransactions())
            return today;

        return null;
    }

    public void accept(Transaction tr) {
        today.apply(tr);
    }

    public void close() {
        today.close();
    }

    public void nextDay() {
        yesterday = today;
        today = yesterday.open();
    }

    private Optional<Float> generateInitialStocks() {
        Random r = new Random();
        float value = Math.round(r.nextFloat()*10);

        if (value < 1)
            Optional.absent();

        return Optional.of(value);
    }
}
