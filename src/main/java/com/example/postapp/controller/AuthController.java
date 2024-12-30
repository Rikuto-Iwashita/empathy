package com.example.postapp.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.postapp.model.User;
import com.example.postapp.service.UserService;

@Controller
@RequestMapping("/")
public class AuthController {
	private final UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("gender", List.of("男性", "女性"));
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signupSubmit(@ModelAttribute User user) {
		userService.registerUser(user);
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String loginForm() {
		return "login";
	}
}
