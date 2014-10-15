package ru.yasuchenin.turtilla.web;


import org.springframework.core.*; 
import org.springframework.web.*; 
import org.springframework.web.servlet.*; 
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 
import java.io.IOException; 

public class Controller { 
    protected final Log logger = LogFactory.getLog(getClass()); 

    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException { 
        logger.info("Returning hello view"); 
        return new ModelAndView("hello.jsp"); 
    } 
}