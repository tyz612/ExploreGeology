package com.geology.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuredController {

    @GetMapping("/secure")
    public String secureMethod() {
        return "This is a secure message";
    }
}
