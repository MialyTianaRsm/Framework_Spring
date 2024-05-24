package mg.itu.prom16 ;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import annotation.Controller;
import annotation.Get;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.Mapping;
import util.Utilitaire;

public class FrontController extends HttpServlet {
    HashMap<String , Mapping> urlMapping ; 
    Vector<String> controllers ; 

    public void init(){
        urlMapping = new HashMap<>();
        try{
            controllers = new Utilitaire().getList(this.getInitParameter("package"), Controller.class);
        }catch(Exception e){
            throw new RuntimeException();
        }
        try {
            for(String controller : controllers){
                Class<?> clazz = Class.forName(controller);
                for (Method method : clazz.getDeclaredMethods()){
                    if(method.isAnnotationPresent(Get.class)){
                        Get getAnnotation = method.getAnnotation(Get.class);
                        String url = getAnnotation.value(); 
                        urlMapping.put(url , new Mapping(controller, method.getName()));
                    }
                }
            }
        }catch(Exception e){
            throw new RuntimeException();
        }
    }
 
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
        // try (PrintWriter out = response.getWriter()) {
        //   out.println("Tonga etooooo!");
        // } catch (Exception e) {
        //     e.printStackTrace(response.getWriter());
        // }
        PrintWriter out = response.getWriter();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        Mapping mapping = urlMapping.get(path);
        if(mapping != null){
            out.println("url : "+path);
            out.println("mapping : "+ mapping);
        }else {
            out.print("Pas de methode");
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | ServletException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | ServletException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}