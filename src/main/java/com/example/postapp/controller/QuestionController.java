package com.example.postapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.postapp.model.Question;
import com.example.postapp.model.User;
import com.example.postapp.service.QuestionService;
import com.example.postapp.service.UserService;

@Controller
@RequestMapping("/empathy")
public class QuestionController {


    private final QuestionService questionService;
    private final UserService userService;

    public QuestionController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }
	
    //質問一覧画面の表示
    @GetMapping("/questions")
    public String showQuestions(@AuthenticationPrincipal User user, Model model) {
    	//ログインユーザーの世代に関する質問を取得
    	List<Question> questions = questionService.getFilteredQuestions(user);
    	model.addAttribute("questions", questions);
    	return "questions";
    }
    
    //質問作成フォーム
    @GetMapping("/questions/create")
    public String createQuestionForm(Model model) {
    	model.addAttribute("question", new Question());
    	return "create-question";
    }
    
    //新しい質問を作成
    @PostMapping("/questions/create")
    public String createQuestion(@AuthenticationPrincipal User user,
    							 @RequestParam String title,
    							 @RequestParam String content,
    							 @RequestParam String ageGroup) {
    	questionService.createQuestion(title, content, ageGroup, user);
    	return "redirect:/empathy/questions";
    }
    
    //質問の詳細画面表示
    @GetMapping("/questions/{id}")
    public String getQuestionDetail(@PathVariable Long id, Model model) {
    	Question question = questionService.getQuestionById(id);
    	
    	List<Map<String, String>> formattedReplies = question.getReplies().stream()
    			.map(reply -> {
    				Map<String, String> replyMap = new HashMap<>();
    				replyMap.put("user", reply.getUser() != null ? reply.getUser().getUsername() : "匿名");
    				replyMap.put("content", reply.getContent() != null ? reply.getContent() : "内容なし");
    				return replyMap;
    			})
    			.collect(Collectors.toList());
    	
    	model.addAttribute("question", question);
    	model.addAttribute("formattedReplies", formattedReplies);
    	return "question-detail";
    }
    
}
