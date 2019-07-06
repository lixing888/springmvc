package com.controller;

import com.annotation.DController;
import com.annotation.RequestMapping;

@DController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(){
        System.out.println("访问到了我！");
        return "";
    }


    @RequestMapping("/world")
    public String ppp(){
        System.out.println("访问到了我world！");
        return "";
    }
}
