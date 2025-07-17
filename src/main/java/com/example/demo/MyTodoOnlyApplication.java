package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MyTodoOnlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyTodoOnlyApplication.class, args);
	
		   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		   String rawPassword = "nnmT0613";
		   String hashed = encoder.encode(rawPassword);
		   System.out.println(hashed); 
		

	}

}
