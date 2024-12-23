package com.example.postapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.postapp.model.Question;
import com.example.postapp.model.User;
import com.example.postapp.service.QuestionService;
import com.example.postapp.service.ReplyService;

@Controller
@RequestMapping("/empathy")
public class ReplyController {
	
	private final ReplyService replyService;
	private final QuestionService questionService;
	
	public ReplyController(ReplyService replyService, QuestionService questionService) {
		this.replyService = replyService;
		this.questionService = questionService;
	}
	
	//返信を投稿
	@PostMapping("/questions/{id}/reply")
	public String postReply(@PathVariable Long id,
							@RequestParam String content,
							@AuthenticationPrincipal User user) {
		Question question = questionService.getQuestionById(id);
		replyService.saveReply(content, question, user);
		return "redirect:/empathy/questions/" + id;
	}
}
