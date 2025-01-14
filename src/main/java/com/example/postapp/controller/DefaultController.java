package com.example.postapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
	
	@GetMapping("/")
	public String redirectToLogin() {
		return "redirect:/login";
	}
}
