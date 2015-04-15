package de.maredit.tar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.maredit.tar.services.AuthenticationService;

@Controller
public class LoginController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @RequestMapping("/login")
    public String login() {
        System.out.println(authenticationService.authenticated("foo", "bar"));
        return "login";
    }
}