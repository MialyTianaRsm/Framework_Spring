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
    String huhu ; 

    public void init(){
        String packageName = this.getInitParameter("package"); 
        huhu = packageName ; 
        Utilitaire util = new Utilitaire();
        try{
            // urlMapping = util.getMapping(packageName, Controller.class);
            urlMapping = new HashMap<String , Mapping>();
            urlMapping.put("/get", new Mapping("ControllerTest", "getMethod"));
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        
        }

    }
 
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ClassNotFoundException {
        PrintWriter out = response.getWriter();
        out.println("Processing request...");
        out.println("huhu"+huhu);
        String url = new Utilitaire().modified_url(request.getRequestURL().toString());
        out.println("Modified URL: " + url);
        out.println(urlMapping);
        Mapping mapping = urlMapping.get(url);
        
        if (mapping == null) {
            out.println("No mapping found for URL: " + url);
            return;
        }
    
        out.println("Class Name: " + mapping.getClassName());
        out.println("Method Name: " + mapping.getMethodName());
        
        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getDeclaredMethod(mapping.getMethodName());
            String result = (String) method.invoke(instance);
            out.println("Result: " + result);
        } catch (Exception e) {
            out.println("Exception: " + e.getMessage());
            e.printStackTrace(out);
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