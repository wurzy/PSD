package directory.representations;

import com.fasterxml.jackson.annotation.*;

public class InfectedRepresentation {
    public final int infected;
    public final String district;

    @JsonCreator
    public InfectedRepresentation(@JsonProperty("district") String d, @JsonProperty("infected") int t) {
        this.infected = t;
        this.district = d;
    }
}
