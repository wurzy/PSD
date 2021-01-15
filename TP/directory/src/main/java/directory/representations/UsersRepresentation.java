package directory.representations;

import com.fasterxml.jackson.annotation.*;

public class UsersRepresentation {
    public final int number;
    public final String district;

    @JsonCreator
    public UsersRepresentation(@JsonProperty("district") String d, @JsonProperty("users") int t) {
        this.number = t;
        this.district = d;
    }
}
