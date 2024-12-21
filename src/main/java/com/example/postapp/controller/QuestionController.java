package com.example.postapp.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.postapp.model.Question;
import com.example.postapp.model.Reply;
import com.example.postapp.model.User;
import com.example.postapp.repository.QuestionRepository;
import com.example.postapp.repository.UserRepository;
import com.example.postapp.service.QuestionService;
import com.example.postapp.service.ReplyService;

@Controller
@RequestMapping("/empathy")
public class QuestionController {

    private final QuestionService questionService;
    private final ReplyService replyService;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    


    public QuestionController(QuestionService questionService, ReplyService replyService, UserRepository userRepository,
			QuestionRepository questionRepository) {
		this.questionService = questionService;
		this.replyService = replyService;
		this.userRepository = userRepository;
		this.questionRepository = questionRepository;
	}

	@GetMapping("/questions")
    public String getAllQuestions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        return "questions";
    }

    @GetMapping("/questions/create")
    public String createQuestionForm() {
        return "create-question";
    }

    @PostMapping("/questions/create")
    public String createQuestion(@ModelAttribute Question question) {
        // ログイン中のユーザーを取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // ユーザーを取得し、存在しない場合は例外をスロー
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + username));
        
        question.setUser(user);
        question.setCreatedAt(LocalDateTime.now());

        // 質問を保存
        questionRepository.save(question);

        return "redirect:/empathy/questions";
    }


    @GetMapping("/questions/{id}")
    public String getQuestionDetail(@PathVariable Long id, Model model) {
        Question question = questionService.getQuestionById(id);
        model.addAttribute("question", question);
        model.addAttribute("replies", question.getReplies());//返信を取得
        return "question-detail";
    }
    
    @PostMapping("/questions/{id}/reply")
    public String postReply(@PathVariable Long id, @RequestParam String content) {
    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	 String username = authentication.getName();
    	 
    	 User user = userRepository.findByUsername(username)
    			 				   .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません:" + username));
    	 
    	 //質問を取得し、無ければ例外をスロー
    	 Question question = questionRepository.findById(id)
    			 							   .orElseThrow(() -> new RuntimeException("質問が見つかりません" + id));
    	 
    	 //返信を作成
    	 Reply reply = new Reply();
    	 reply.setContent(content);
    	 reply.setCreatedAt(LocalDateTime.now());
    	 reply.setUser(user);
    	 reply.setQuestion(question);
    	 
    	 //返信を保存
    	 replyService.saveReply(reply);
    	 
    	 //返信したら、質問詳細ページに移動
    	 return "redirect:/empathy/questions/" + id;
    }
    
    @GetMapping("/questions/{id}/reply")
    public String getReplyForm(@PathVariable Long id, Model model) {
    	//質問をIDで取得
    	Question question = questionService.getQuestionById(id);
    	model.addAttribute("question" ,question);
    	return "reply-form";
    }
}
