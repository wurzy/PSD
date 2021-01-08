package models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Stock {
    private Optional<Float> minimum, maximum, open, close;

    @JsonIgnore
    private Optional<Float> current;

    public Stock(Optional<Float> initial) {
        open = initial;
        minimum = initial;
        maximum = initial;
        current = initial;

        close = Optional.absent();
    }

    public Float getMinimum() {
        return minimum.orNull();
    }

    public Float getMaximum() {
        return maximum.orNull();
    }

    public Float getOpen() {
        return open.orNull();
    }

    public Float getClose() {
        return close.orNull();
    }

    public void apply(Transaction tr) {
        float price = tr.getPrice();

        if (tr.getPrice() > maximum.or(Float.MIN_VALUE))
            maximum = Optional.of(price);

        if (tr.getPrice() < minimum.or(Float.MAX_VALUE))
            minimum = Optional.of(price);

        current = Optional.of(price);
    }

    public void close() {
        close = Optional.fromNullable(current.orNull());
    }

    public Stock open() {
        return new Stock(close);
    }

    public boolean hasTransactions() {
        return current.isPresent() && open.isPresent();
    }
}