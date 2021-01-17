package Directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Top5LocationsRepresentation {
    public final String district;
    public final String location;
    public final int concentration;

    @JsonCreator
    public Top5LocationsRepresentation(@JsonProperty("district") String d, @JsonProperty("location") String t, @JsonProperty("concentration") int c) {
        this.district = d;
        this.location = t;
        this.concentration = c;
    }
}
