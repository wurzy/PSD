package Directory.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.media.sound.InvalidDataException;
import Directory.business.*;
import Directory.representations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("api")
public class DirectoryResource {
    private DirectoryClass diretorio;

    public DirectoryResource(DirectoryClass diretorio){
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

    @GET
    @Path("/districts/avg")
    @Produces(MediaType.APPLICATION_JSON)
    public AvgRepresentation getContactedInfectedAvg() {
        synchronized (this){
            return new AvgRepresentation(this.diretorio.getContactedInfectedAvg());
        }
    }

    @GET
    @Path("/districts/top5infected")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Top5InfectedRepresentation> getTop5Infected() {
        ArrayList<Top5InfectedRepresentation> al = new ArrayList<>();
        synchronized (this){
            LinkedHashMap<String,Double> lhm = this.diretorio.getTop5Infected();
            for(Map.Entry<String,Double> entry : lhm.entrySet()){
                al.add(new Top5InfectedRepresentation(entry.getKey(),entry.getValue()));
            }
        }
        return al;
    }

    @GET
    @Path("/districts/top5locations")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Top5LocationsRepresentation> getTop5Locations() {
        ArrayList<Top5LocationsRepresentation> al = new ArrayList<>();
        synchronized (this){
            LinkedHashMap<String,Integer> lhm = this.diretorio.getTop5Concentration();
            lhm.forEach((key,value) -> {
                String[] parts = key.split("~");
                al.add(new Top5LocationsRepresentation(parts[1],parts[0],value));
            });
        }
        return al;
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

    @POST
    @Path("/districts/{id}/concentration")
    public Response addConcentration(@PathParam("id") int id, PostConcentration conc) {
        synchronized (this) {
            try {
                this.diretorio.addConcentration(id,conc);
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

    public static class PostUser {
        @JsonProperty("coordx")
        public int coordx;
        @JsonProperty("coordy")
        public int coordy;
        @JsonProperty("user")
        public String user;
    }

    public static class PostConcentration {
        @JsonProperty("coordx")
        public int coordx;
        @JsonProperty("coordy")
        public int coordy;
        @JsonProperty("concentration")
        public int concentration;
    }
}

