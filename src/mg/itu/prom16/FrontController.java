package mg.itu.prom16 ;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Vector;


import annotation.Controller;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Utilitaire;

public class FrontController extends HttpServlet {
    String packagePath ; 
    boolean isChecked ; 
    Vector<String> controllers ; 

    public void init(){
        try{
            controllers = new Utilitaire().getListController(this.getInitParameter("package"), Controller.class);
            isChecked = true ; 
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
        if(!isChecked){
            init();
        }else{
            for(int i = 0 ; i < controllers.size() ; i++){
                out.println(controllers.get(i));
            }
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