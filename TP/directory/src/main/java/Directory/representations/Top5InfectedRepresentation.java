package Directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Top5InfectedRepresentation {
    public final String district;
    public final double ratio;

    @JsonCreator
    public Top5InfectedRepresentation(@JsonProperty("district") String d, @JsonProperty("ratio") double t) {
        this.district = d;
        this.ratio = t;
    }
}
