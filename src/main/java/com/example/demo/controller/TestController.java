package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/error-test")
    public String testError() {
        throw new IllegalStateException("予期せぬエラーが発生しました！");
    }
}
