package ismael.t.npi.myapplication;

import java.util.ArrayList;

public class Animal {
    private String name;
    private String image;
    private String info;
    private int id;

    /**
     * @brief Constructor por defecto del Animal
    */

    public Animal(){
        name = ""; //Animal por defecto
        image = "";
        info  = "";
        id = 0;
    }

    /**
     * @brief Constructor que inicializa los campos del animal
     */

    public Animal(ArrayList<String> values, int identificador){
        if (!values.isEmpty()){
            name = values.get(0);
            image = values.get(1);
            info = values.get(2);
            id = identificador;
        }
    }

    /**
     * @brief Conjunto de gets para los diferentes campos
     */

    public String getName() { return name;  }
    public String getImage(){ return image; }
    public String getInfo() { return info;  }
    public String getTag()  { return name;  }
    public  int   getId ()  { return id;    }

    /**
     * @brief Funcion para copiar un animal en otro
     */

    public void Copy(Animal animal){
        name = animal.getName();
        image = animal.getImage();
        info = animal.getInfo();
        id = animal.getId();
    }
}
