package util;


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;

import exception.AnnotationNotPresentException;
import exception.DuplicateUrlException;
import exception.ExceptionHandler;
import exception.InvalidControllerProviderException;
import exception.InvalidRequestException;
import exception.IllegalReturnTypeExcpetion;
import exception.UrlNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.itu.prom16.FrontController;

public class MainProcess {
    static FrontController frontController;
    private List<Exception> exceptions;

    private static String handleRest(Object methodObject, HttpServletResponse response) {
        Gson gson = new Gson();
        String json = null;
        if (methodObject instanceof ModelAndView) {
            json = gson.toJson(((ModelAndView)methodObject).getData());
        } else {
            json = gson.toJson(methodObject);
        }   
        response.setContentType("application/json");
        return json;
    }

    public static void handleRequest(FrontController controller, HttpServletRequest request,
            HttpServletResponse response) throws IOException, UrlNotFoundException, ClassNotFoundException,
            NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, ServletException, IllegalReturnTypeExcpetion, NoSuchFieldException, AnnotationNotPresentException, InvalidRequestException {
        PrintWriter out = response.getWriter();
        String verb = request.getMethod();

        if (controller.getException() != null) {
            ExceptionHandler.handleException(controller.getException(), response);
            return;
        }

        String url = request.getRequestURI().substring(request.getContextPath().length());
        Mapping mapping = frontController.getURLMapping().get(url);
        
        if (mapping == null) {
            throw new UrlNotFoundException("Oops, url not found!");
        }
        
        VerbMethod verbMethod = mapping.getSpecificVerbMethod(verb);
        
        Object result = ReflectUtils.executeRequestMethod(mapping, request, verb);

        if (verbMethod.isRestAPI()) {
            result = handleRest(result, response);
        }   

        if (result instanceof String) {
            out.println(result.toString());
        } else if (result instanceof ModelAndView) {
            ModelAndView ModelAndView = ((ModelAndView) result);
            HashMap<String, Object> data = ((HashMap<String, Object>)ModelAndView.getData());

            for (Entry<String, Object> entry : data.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.getRequestDispatcher(ModelAndView.getUrl()).forward(request, response);
        } else {
            throw new IllegalReturnTypeExcpetion("Invalid return type");
        }
    }

    public static void init(FrontController controller)
            throws ClassNotFoundException, IOException, DuplicateUrlException, InvalidControllerProviderException {
        frontController = controller;

        String packageName = controller.getInitParameter("package_name");

        HashMap<String, Mapping> urlMappings;
        urlMappings = (HashMap<String, Mapping>) PackageScanner.scanPackage(packageName);

        controller.setURLMapping(urlMappings);
    }

    // Getters and setters
    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }
}