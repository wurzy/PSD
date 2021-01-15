package directory.resources;

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
    public UsersRepresentation getEmpresas(@PathParam("id") int dist) {
        synchronized (this){
        }
    return null;
    }

    @POST
    @Path("/districts/{id}")
    public Response addUser(@PathParam("id") int id) {
        synchronized (this) {
            this.diretorio.endEmprestimo(e);
        }
        return Response.ok().build();
    }
}

