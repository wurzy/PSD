package directory.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.media.sound.InvalidDataException;
import directory.business.*;
import directory.representations.InfectedRepresentation;
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

    @GET
    @Path("/districts/{id}/infected")
    @Produces(MediaType.APPLICATION_JSON)
    public InfectedRepresentation getInfected(@PathParam("id") int dist) {
        synchronized (this){
            try{
                int t = this.diretorio.getNumberOfInfected(dist);
                return new InfectedRepresentation(diretorio.getNameOfDistrict(dist),t);
            }
            catch(Exception e){
                return null;
            }
        }
    }

    @POST
    @Path("/districts/{id}/users")
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

    @DELETE
    @Path("/districts/{id}/users/{user}")
    public Response deleteUser(@PathParam("id") int id, @PathParam("user") String user) {
        synchronized (this) {
            try {
                this.diretorio.deleteUser(id,user);
                return Response.ok().build();
            }
            catch(InvalidDataException e1){
                return invalidDistrict();
            }
            catch(InputMismatchException e2){
                return invalidUser();
            }
        }
    }

    @PUT
    @Path("/districts/{id}/infected/{user}")
    public Response userInfected(@PathParam("id") int id, @PathParam("user") String user) {
        synchronized (this) {
            try {
                this.diretorio.infectedUser(id,user);
                return Response.ok().build();
            }
            catch(InvalidDataException e1){
                return invalidDistrict();
            }
            catch(InputMismatchException e2){
                return invalidUser();
            }
        }
    }

    private Response invalidDistrict(){
        return Response.status(404).entity("Não existe o distrito especificado.").build();
    }

    private Response invalidUser(){
        return Response.status(404).entity("Não existe o utilizador especificado.").build();
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

