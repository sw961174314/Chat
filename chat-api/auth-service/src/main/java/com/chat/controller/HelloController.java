package com.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class HelloController {

    @GetMapping("hello")
    public Object Helllo() {
        return "Hello world~";
    }
}