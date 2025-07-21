package com.example.demo.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class) // 全例外を捕まえる
	public String handleException(Exception ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "error/customError"; // templates/error/customError.html を返す
	}
}