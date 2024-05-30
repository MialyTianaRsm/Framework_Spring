package util ;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import annotation.Get;

public class Utilitaire {
    public String modifyPath (String path) {
        path = path.substring(1);
        path = path.replace("%20", " ");
        return path ; 
    }

    // public Vector<String> getList(String packageName , Class<? extends Annotation> annotation) throws ClassNotFoundException {
    //     Vector<String> controllers = new Vector<String>();
    //     packageName.replace(".", "/");
    //     try{

    //         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    //         java.net.URL source = classLoader.getResource(packageName);
    
    //         String realPath = this.modifyPath(source.getFile());
    
    //         File classPathDirectory = new File(realPath);
    //         if(classPathDirectory.isDirectory()){
    //             packageName = packageName.replace("/", ".");
    //             for(String fileName : classPathDirectory.list()){
    //                 fileName = fileName.substring( 0,fileName.length()-6);
    //                 String className = packageName +"."+ fileName ; 
    //                 Class<?> clazz = Class.forName(className);
    //                 if(clazz.isAnnotationPresent(annotation)){
    //                     controllers.add(className);
    //                 }
    //             }
    //         }
    //     }catch(ClassNotFoundException e){
    //         System.out.println(e.getMessage());
    //     }
    //     return controllers ; 
        
    // }
    public Vector<String> getList(String packageName , Class<? extends Annotation> annotation) throws ClassNotFoundException {
        Vector<String> controllers = new Vector<String>();
        String path = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL ressource = classLoader.getResource(path);
        if( ressource == null ){
            throw new ClassNotFoundException("Package = "+ packageName + "not found ");
        }

        String realPath = this.modifyPath(ressource.getFile());
        File classPathDirectory = new File(realPath);
        if(!classPathDirectory.exists() || !classPathDirectory.isDirectory()){
            throw new ClassNotFoundException("Package = "+ packageName + "not found ");
        }

        for(String fileName : classPathDirectory.list()){
            if(fileName.endsWith(".class")){
                String className = packageName + "." +fileName.substring(0 , fileName.length()-6);
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(annotation)){
                    controllers.add(className);
                }

            }
        }
        return controllers ; 
        
    }

    public HashMap<String , Mapping> getMapping (String packageName , Class<?extends Annotation> annotation) throws ClassNotFoundException{
        Vector<String> controllerList = getList(packageName, annotation);
        System.out.println(controllerList);
        HashMap<String,Mapping> classesList = new HashMap<>();
        for( int i = 0 ; i < controllerList.size() ; i++){
            try {
                String className = controllerList.get(i) ; 
                Class<?> clazz = Class.forName(className);
                Method[] method = clazz.getDeclaredMethods();
                for(int j = 0 ; j < method.length ; j++){
                    if(method[j].isAnnotationPresent(Get.class)){
                        Mapping value = new Mapping(className, method[j].getName());
                        String key = method[j].getAnnotation(Get.class).value();
                        classesList.put(key, value) ; 
                         System.out.println(key);
                         System.out.println(value.getClassName());
                         System.out.println(value.getMethodName());
                    }
                }            
            }catch(Exception e){

            }
        }
        return classesList ; 
    }

    public String modified_url (String url){
        String newUrl = "/";
        String[] path1 = url.split("//");
        String[] path = path1[1].split("/");
        for(int i = 2 ; i < path.length ; i++){
            newUrl += path[i]+"/";
        }
        url = newUrl.substring( 0 , newUrl.length()-1);
        return url ;
    }
}