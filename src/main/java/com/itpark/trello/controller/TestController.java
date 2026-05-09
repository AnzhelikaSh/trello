package com.itpark.trello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Привет! Tomcat работает! 🚀";
    }

    @GetMapping("/test")
    public String test() {
        return "Тестовый эндпоинт работает!";
    }
}
