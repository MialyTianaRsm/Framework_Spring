import java.lang.annotation.Annotation;
import java.util.HashMap;

import annotation.Controller;
import util.Mapping;
import util.Utilitaire;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        HashMap<String , Mapping> map = new Utilitaire().getMapping("Controller", Controller.class);
        System.out.println(map);
        
    }
}
