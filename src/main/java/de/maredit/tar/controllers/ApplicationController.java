package de.maredit.tar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.maredit.tar.repositories.UserRepository;

@Controller
public class ApplicationController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private Environment environment;
    
    @RequestMapping("/")
    public String index() {
        System.out.println("Foo: " + environment.getProperty("spring.url"));
        
        
        return "application/index";
    }
}