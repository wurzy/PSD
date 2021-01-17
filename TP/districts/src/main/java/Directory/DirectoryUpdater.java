package Directory;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

class PostUser{
    public int coordx;
    public int coordy;
    public String user;

    public PostUser(int x, int y, String s){
        this.coordx = x;
        this.coordy = y;
        this.user = s;
    }
}

class PostConcentration {
    public int coordx;
    public int coordy;
    public int concentration;

    public PostConcentration(int coordx, int coordy, int concentration) {
        this.coordx = coordx;
        this.coordy = coordy;
        this.concentration = concentration;
    }
}

public class DirectoryUpdater {
    private HttpClient http;
    private String url;
    private Gson gson;

    public DirectoryUpdater(int port){
        this.url = "http://localhost:" + port + "/api/";
        this.http = HttpClientBuilder.create().build();
        this.gson = new Gson();
    }

    public void addUser(String user, int coordx, int coordy, int district){
        try{
            HttpPost p = new HttpPost(url + "districts/" + district + "/users");
            PostUser u = new PostUser(coordx,coordy,user);
            StringEntity data = new StringEntity(gson.toJson(u));
            p.setEntity(data);
            p.setHeader("Content-Type", "application/json");
            http.execute(p);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateConcentration(int coordx, int coordy, int concentration, int district){
        try{
            HttpPost p = new HttpPost(url + "districts/" + district + "/concentration");
            PostConcentration c = new PostConcentration(coordx,coordy,concentration);
            StringEntity data = new StringEntity(gson.toJson(c));
            p.setEntity(data);
            p.setHeader("Content-Type", "application/json");
            http.execute(p);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sickUser(String user, int district){
        try{
            HttpPut p = new HttpPut(url + "districts/" + district + "/infected/" + user);
            p.setHeader("Content-Type", "application/json");
            http.execute(p);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
