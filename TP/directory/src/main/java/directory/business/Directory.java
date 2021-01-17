package directory.business;

import com.sun.media.sound.InvalidDataException;
import directory.resources.DirectoryResource.*;

import java.util.*;
import java.util.stream.Collectors;

public class Directory {

    private static Directory instance = null;

    public static Directory getInstance(){
        if (instance == null){
            instance = new Directory();
        }

        return instance;
    }

    private Map<Integer, District> districts;
    private Map<Integer, String> distNames;

    public Directory(){
        this.districts = new HashMap<>();
        this.distNames = new HashMap<>();

        this.distNames.put(1,"Aveiro");
        this.distNames.put(2,"Beja");
        this.distNames.put(3,"Braga");
        this.distNames.put(4,"Bragança");
        this.distNames.put(5,"Castelo Branco");
        this.distNames.put(6,"Coimbra");
        this.distNames.put(7,"Évora");
        this.distNames.put(8,"Faro");
        this.distNames.put(9,"Guarda");
        this.distNames.put(10,"Leiria");
        this.distNames.put(11,"Lisboa");
        this.distNames.put(12,"Portalegre");
        this.distNames.put(13,"Porto");
        this.distNames.put(14,"Santarém");
        this.distNames.put(15,"Setúbal");
        this.distNames.put(16,"Viana do Castelo");
        this.distNames.put(17,"Vila Real");
        this.distNames.put(18,"Viseu");

        for(int i = 1; i < 19; i++){
            this.districts.put(i,new District(this.distNames.get(i)));
        }
    }

    public int getNumberOfUsers(int district) throws Exception{
        District d = this.districts.get(district);
        if(d==null) throw new Exception();
        return d.getNumberOfUsers();
    }

    public int getNumberOfInfected(int district) throws Exception{
        District d = this.districts.get(district);
        if(d==null) throw new Exception();
        return d.getNumberOfInfected();
    }

    public String getNameOfDistrict(int id){
        return this.distNames.get(id);
    }

    public void userUpdate(int district, PostUser user) throws Exception{
        District d = this.districts.get(district);
        if(d==null) throw new Exception();
        if(!d.userExists(user.user)){
            d.addUser(user.user);
        }
        d.addCoord(user.user,new Point(user.coordx,user.coordy));
    }

    public void deleteUser(int district, String user) throws InvalidDataException,InputMismatchException {
        District d = this.districts.get(district);
        if(d==null) throw new InvalidDataException();
        if(!d.userExists(user)) throw new InputMismatchException();
        d.removeUser(user);
    }

    public void infectedUser(int district, String user) throws InvalidDataException,InputMismatchException{
        District d = this.districts.get(district);
        if(d==null) throw new InvalidDataException();
        if(!d.userExists(user)) throw new InputMismatchException();
        d.sick(user);
    }

    public double getContactedInfectedAvg(){
        double avg = 0;
        for(Map.Entry<Integer,District> dists: this.districts.entrySet()){
            District d = dists.getValue();
            avg +=  d.getContactedInfected();
        }
        return avg/this.districts.size();
    }

    public void addConcentration(int id, PostConcentration pc) throws Exception{
        District d = this.districts.get(id);
        if (d==null) throw new Exception();
        d.setConcentrationMax(new Point(pc.coordx,pc.coordy),pc.concentration);
    }

    public LinkedHashMap<String,Double> getTop5Infected(){
        HashMap<String, Double> map = new HashMap<>();
        for(Map.Entry<Integer,District> dists: this.districts.entrySet()){
            District d = dists.getValue();
            map.put(d.getName(),d.getRatio());
        }
        return map.entrySet()
                .stream()
                .filter(e -> !Double.isNaN(e.getValue()))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)
                );
    }

    public LinkedHashMap<String,Integer> getTop5Concentration(){
        HashMap<String,Integer> map = new HashMap<>();
        HashMap<String,Integer> dist;
        for(Map.Entry<Integer,District> dists: this.districts.entrySet()){
            District d = dists.getValue();
            dist = d.getConcentrationMax();
            dist.forEach((key,value) -> {
                if(!map.containsKey(key) || value > map.get(key)){
                    map.put(key + "~" + d.getName(),value);
                }
            });
        }
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)
                );
    }

    private void testeInit(District d){
        d.addUser("1");
        d.addUser("2");
        d.addUser("3");
        d.addUser("4");
        d.addUser("5");
        d.addUser("6");
        d.addUser("7");
        Random r = new Random();
        int x = r.nextInt(7);
        for(int i = 0; i < x; i++){
            d.sick(String.valueOf(i+1));
        }
    }
}
