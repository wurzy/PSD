package directory.business;

import java.util.*;

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

        for(int i = 1; i < 19; i++){
            this.districts.put(i,new District());
        }

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
    }
}
