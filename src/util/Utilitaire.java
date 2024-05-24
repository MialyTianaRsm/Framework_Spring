package util ;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Vector;


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
}