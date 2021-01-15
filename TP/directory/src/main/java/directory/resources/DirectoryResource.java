package directory.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import directory.business.*;
import directory.representations.UsersRepresentation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("api")
public class DirectoryResource {
    private Directory diretorio;

    public DirectoryResource(Directory diretorio){
        this.diretorio = diretorio;
    }

    @GET
    @Path("/districts/{id}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public UsersRepresentation getUsers(@PathParam("id") int dist) {
        synchronized (this){
            try{
                int t = this.diretorio.getNumberOfUsers(dist);
                return new UsersRepresentation(diretorio.getNameOfDistrict(dist),t);
            }
            catch(Exception e){
                return null;
            }
        }
    }

    @POST
    @Path("/districts/{id}")
    public Response addUser(@PathParam("id") int id, PostUser user) {
        synchronized (this) {
            try {
                this.diretorio.userUpdate(id,user);
                return Response.ok().build();
            }
            catch(Exception e){
                return invalidDistrict();
            }
        }
    }

    private Response invalidDistrict(){
        return Response.status(404).entity("Não existe o distrito especificado.").build();
    }
    public static class PostUser{
        @JsonProperty("coordx")
        public int coordx;
        @JsonProperty("coordy")
        public int coordy;
        @JsonProperty("user")
        public String user;
    }
}

