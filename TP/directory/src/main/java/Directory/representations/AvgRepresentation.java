package Directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AvgRepresentation {
    public final double average;

    @JsonCreator
    public AvgRepresentation(@JsonProperty("average") double t) {
        this.average = t;
    }
}
