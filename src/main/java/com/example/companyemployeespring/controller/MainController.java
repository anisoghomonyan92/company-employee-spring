package com.example.companyemployeespring.controller;
import org.springframework.web.bind.annotation.GetMapping;

public class MainController {
    @GetMapping(value = "/")
    public String massage() {
        return "index";
    }
}
