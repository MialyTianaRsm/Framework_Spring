package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import annotation.Controller;
import annotation.ReqParam;
import exception.DuplicateUrlException;
import exception.InvalidReturnTypeExcpetion;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Mapping;
import util.ModelAndView;
import util.TypeConverter;
import util.Utilitaire;

public class FrontController extends HttpServlet {
    private Utilitaire scanner; 
    HashMap<String, Mapping> urlMappings;

    public void init() throws ServletException {
        // try {
        //     scanner = new Utilitaire();
        //     String packageName = getInitParameter("package");
        //     urlMappings = scanner.getMapping(packageName, Controller.class);

        //     if (packageName == null || packageName.isEmpty()) {
        //         throw new InvalidControllerProviderException("Invalid Controller Provider ");
        //     }
        //     Vector<String> controllers = Utilitaire.getListController(packageName, Controller.class);
        //     HashMap<String, Mapping> temp = Utilitaire.getMapping(controllers);
        //     setUrlMappings(temp);
        // } catch (Exception e) {
        //     throw new ServletException(e);
        // }
        try{
            scanner = new Utilitaire();
            String packagename = this.getInitParameter("package");
            urlMappings = scanner.getMapping(packagename, Controller.class);
        } catch (DuplicateUrlException e) {
            log("DuplicateGetMappingException occurred: " + e.getMessage());
        }

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException {
        // PrintWriter out = response.getWriter();
        // try {
        //     String url = request.getRequestURI().substring(request.getContextPath().length());
        //     Mapping mapping = urlMappings.get(url);
        //     out.println(mapping);
        //     if (mapping != null) {
        //         Class<?> clazz = Class.forName(mapping.getClassName());
        //         Method method = mapping.getMethod();
        //         Object result = mapping.invoke(request, clazz, method);
        //         if (result instanceof String) {
        //             out.println(result);
        //         // } else if (result instanceof ModelAndView) {
        //             ModelAndView modelAndView = (ModelAndView) result;
        //             HashMap<String, Object> data = modelAndView.getData();
        //             for (String key : data.keySet()) {
        //                 request.setAttribute(key, data.get(key));
        //             }
        //             request.getRequestDispatcher(modelAndView.getUrl()).forward(request, response);
        //         } else {
        //             throw new InvalidReturnTypeExcpetion("Invalid return type");
        //         }
        //     } else {
        //         throw new UrlNotFoundException("Url not found ");
        //     }
        // } catch (UrlNotFoundException e) {
        //     response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        // } catch (Exception e) {
        //     response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        // }
        PrintWriter out = response.getWriter();
        String url = scanner.conform_url(request.getRequestURL().toString());
        Mapping mapping = urlMappings.get(url);
        if (mapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not mapped.");
            return;
        }

        try {
            Class<?> clazz = Class.forName(mapping.getClassName());
            // Controller instance:
            Object instance = clazz.getDeclaredConstructor().newInstance();
            // get matching method for url
            Method method = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(mapping.getMethodName())) {
                    method = m;
                    break;
                }
            }

            // get All the parameters name from a form:
            Enumeration<String> parameterNames = request.getParameterNames();
            // Map to hold created objects
            Map<String, Object> objets = new HashMap<String, Object>();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String[] parts = paramName.split("\\.");
                if (parts.length > 1) {
                    String objectName = parts[0];
                    Class<?> objetclazz = Class.forName(this.getInitParameter("model-package")+"." + objectName);
                    Object mydataholder;
                    if (!objets.containsKey(objectName)) {
                        mydataholder = objetclazz.getDeclaredConstructor().newInstance();
                        objets.put(objectName, mydataholder);
                    } else {
                        mydataholder = objets.get(objectName);
                    }
                    Method datasetter = null;
                    for (Method m : objetclazz.getDeclaredMethods()) {
                        if (m.getName().equals("set" + parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1))) {
                            datasetter = m;
                            break;
                        }
                    }
                    String paramValue = request.getParameter(paramName);
                    datasetter.invoke(mydataholder, TypeConverter.convertParameter(datasetter.getParameterTypes()[0], paramValue));
                } else {
                    String paramValue = request.getParameter(paramName);
                    objets.put(paramName, paramValue);
                }
            }

            // Prepare method arguments:
            List<Object> methodArgs = new ArrayList<>();
            for (Parameter parameter : method.getParameters()) {
                // String paramName = parameter.isAnnotationPresent(ReqParam.class) ? parameter.getAnnotation(ReqParam.class).value() : parameter.getName();
                String paramName = "" ; 
                if(parameter.isAnnotationPresent(ReqParam.class)) {
                    paramName = parameter.getAnnotation(ReqParam.class).value() ; 
                }else{
                    request.setAttribute("error" , "ETU002593 erreur");
                    RequestDispatcher dispatch = request.getRequestDispatcher("error.jsp");
                    dispatch.forward(request, response);

                }
                methodArgs.add(objets.get(paramName));
            }

            // Invoke the controller method
            Object result = method.invoke(instance, methodArgs.toArray());
            if (result instanceof String) {
                out.println(result);
            } else if (result instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) result;
                mv.getData().forEach(request::setAttribute);
                RequestDispatcher dispatcher = request.getRequestDispatcher(mv.getUrl());
                dispatcher.forward(request, response);
            } else {
                throw new InvalidReturnTypeExcpetion("Return type not handled for: " + url);
            }
        } catch (Exception e) {
            out.println("<h3>Oops!</h3>");
            out.println("<p>An error occurred while processing the request.</p>");
            out.println(e);
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
            e.printStackTrace();
        }
    }

    /**
     * @return the urlMappings
     */
    public HashMap<String, Mapping> getUrlMappings() {
        return urlMappings;
    }

    /**
     * @param urlMappings the urlMappings to set
     */
    public void setUrlMappings(HashMap<String, Mapping> urlMappings) {
        this.urlMappings = urlMappings;
    }

}