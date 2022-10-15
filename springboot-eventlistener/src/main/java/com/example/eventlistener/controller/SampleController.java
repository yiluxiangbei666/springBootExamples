package com.example.eventlistener.controller;

import com.example.eventlistener.service.impl.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api")
public class SampleController {

    @Resource
    private UserService userService;

    @GetMapping(value = "/test")
    public void test() {
        userService.register();
    }

}
