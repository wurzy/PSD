package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InfectedRepresentation {
    private final int number;
    private final String district;

    @JsonCreator
    public InfectedRepresentation(@JsonProperty("district") String d, @JsonProperty("infected") int t) {
        this.number = t;
        this.district = d;
    }
}
