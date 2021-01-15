package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsersRepresentation {
    private final int number;
    private final String district;

    @JsonCreator
    public UsersRepresentation(@JsonProperty("district") String d, @JsonProperty("users") int t) {
        this.number = t;
        this.district = d;
    }
}
