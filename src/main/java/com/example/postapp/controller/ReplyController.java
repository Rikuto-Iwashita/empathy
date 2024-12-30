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
	
	//返信を投稿する処理
	@PostMapping("/empathy/replies/{id}/submit")
	public String addReply(
			@PathVariable Long id,
			@RequestParam String content,
			@AuthenticationPrincipal User loggedInUser) {
		
		//質問を取得
		Question question = questionService.getQuestionById(id);
		
		//返信を保存
		try {
			replyService.saveReply(loggedInUser, question, content);
		} catch (IllegalArgumentException e) {
			//返信出来ない場合
			return "error";
		}
		
		// 質問詳細ページにリダイレクト
        return "redirect:/empathy/questions/" + id;
	}
}
