package directory.representations;

import com.fasterxml.jackson.annotation.*;

public class InfectedRepresentation {
    public final int number;
    public final String district;

    @JsonCreator
    public InfectedRepresentation(@JsonProperty("district") String d, @JsonProperty("infected") int t) {
        this.number = t;
        this.district = d;
    }
}
