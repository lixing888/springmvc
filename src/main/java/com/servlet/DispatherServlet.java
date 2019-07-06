package com.servlet;

import com.annotation.DController;
import com.annotation.RequestMapping;
import com.controller.HelloController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DispatherServlet extends HttpServlet {
    private static final List<Object> clazz=new ArrayList<>();
    private static final Map<String,Method> MAPPING=new HashMap<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        int indexOf = requestURI.lastIndexOf("/");
        String substring = requestURI.substring(indexOf);
        Method method = MAPPING.get(substring);
        if(method==null){
            resp.getWriter().println("404");
            return;
        }
        try {
            method.invoke(new HelloController());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        String conf=config.getInitParameter("config");
        ResourceBundle rb=ResourceBundle.getBundle(conf);
        String str= ((DispatherServlet.class.getResource("/")+rb.getString("controller")).replace("file:/","")).replace(".","/");
        File file = new File(str);
        String[] list = file.list();
        for (String s : list) {
            if(s.endsWith(".class")){
                try {
                    Object newInstance = Class.forName((rb.getString("controller")+"." + s).replace(".class", "")).newInstance();
                    clazz.add(newInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //遍历所有的controler包下面类
        for (Object obj : clazz) {
            if(obj.getClass().isAnnotationPresent(DController.class)){
                Method[] methods = obj.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        String key = method.getAnnotation(RequestMapping.class).value();
                        MAPPING.put(key,method);
                    }
                }
            }
        }
    }
}
