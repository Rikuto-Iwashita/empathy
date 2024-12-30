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
import com.example.postapp.service.PostService;
import com.example.postapp.service.QuestionService;
import com.example.postapp.service.ReplyService;
import com.example.postapp.service.UserService;

@Controller
@RequestMapping("/empathy")
public class QuestionController {


    private final QuestionService questionService;
    private final UserService userService;
    private final PostService postService;
    private final ReplyService replyService;
    
    public QuestionController(QuestionService questionService, UserService userService, PostService postService,
			ReplyService replyService) {
		this.questionService = questionService;
		this.userService = userService;
		this.postService = postService;
		this.replyService = replyService;
	}

	//質問一覧画面の表示
    @GetMapping("/questions")
    public String showQuestions(
            @AuthenticationPrincipal User loggedInUser,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "genderFilter", required = false) String genderFilter,
            Model model) {
        // ログインユーザーの年齢を計算
        int userAge = postService.calculateAge(postService.convertToDate(loggedInUser.getDateOfBirth()));

        // ログインユーザーの世代を取得
        String currentAgeGroup = userService.getAgeGroupFormAge(userAge);

        // フィルター条件に応じて質問を取得
        List<Question> questions;
        if ("sameGeneration".equals(filter)) {
            // 同世代向けの質問を取得（その世代からその世代向け）
            questions = questionService.getQuestionsFromAndToSameGeneration(loggedInUser);
        } else if ("toMyGeneration".equals(filter)) {
            // ログインユーザーに向けられた質問を取得
            questions = questionService.getQuestionsToSpecificAgeGroup(loggedInUser);
        } else {
            // 全ての質問を取得
            questions = questionService.getAllQuestions();
        }
        
        //性別フィルターの適応
        if (genderFilter != null && !genderFilter.isBlank()) {
            questions = questions.stream()
                .filter(q -> "指定なし".equals(genderFilter) || genderFilter.equals(q.getGender()))
                .collect(Collectors.toList());
        }

        // 各質問に投稿者の世代情報を追加
        Map<Long, String> questionCreatedByAgeGroups = new HashMap<>();
        for (Question question : questions) {
            User user = question.getUser();
            if (user != null) {
                int age = postService.calculateAge(postService.convertToDate(user.getDateOfBirth()));
                questionCreatedByAgeGroups.put(question.getId(), userService.getAgeGroupFormAge(age));
            } else {
                questionCreatedByAgeGroups.put(question.getId(), "不明");
            }
        }
    	
    	//質問とそれに関連する世代情報をテンプレートに渡す
    	model.addAttribute("questions", questions);
    	model.addAttribute("currentAgeGroup", currentAgeGroup);
    	model.addAttribute("questionCreatedByAgeGroups", questionCreatedByAgeGroups);
    	
    	return "questions";
    }
    
    //質問作成フォーム
    @GetMapping("/questions/create")
    public String createQuestionForm(Model model) {
    	model.addAttribute("question", new Question());
    	model.addAttribute("genders", List.of("男性","女性","指定なし"));
    	return "create-question";
    }
    
    //新しい質問を作成
    @PostMapping("/questions/create")
    public String createQuestion(@AuthenticationPrincipal User user,
    							 @RequestParam String title,
    							 @RequestParam String content,
    							 @RequestParam String ageGroup,
    							 @RequestParam String gender) {
    	questionService.createQuestion(title, content, ageGroup, gender, user);
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
    
    @GetMapping("/questions/{id}/reply")
    public String showReplyForm(@PathVariable Long id, Model model) {
    	Question question = questionService.getQuestionById(id);
    	if(question == null) {
    		return "redirect:/empathy/questions";
    	}
    	model.addAttribute("question", question);
    	return "reply-form";
    }
    
    @PostMapping("/questions/{id}/reply")
    public String submitReply(@PathVariable Long id,
    						  @RequestParam String content,
    						  @AuthenticationPrincipal User user,
    						  Model model) {
    	//質問を取得
    	Question question = questionService.getQuestionById(id);
    	if(question == null) {
    		return "redirect:/empathy/questions";
    	}
    	
    	//性別制限チェック
    	String questionGender = question.getGender();
    	if(!"指定なし".equals(questionGender) && !questionGender.equals(user.getGender())) {
    		model.addAttribute("error", "この質問には回答できません");
    		model.addAttribute("question", question);
    		return "question-detail";//詳細画面にエラーメッセージを表示
    	}
    	
    	//返信を保存
    	replyService.saveReply(user, question, content);
    	return "redirect:/empathy/questions/{id}";
    }
    
}
