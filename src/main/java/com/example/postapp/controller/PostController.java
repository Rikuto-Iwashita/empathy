package com.example.postapp.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.postapp.model.Post;
import com.example.postapp.model.User;
import com.example.postapp.service.PostService;
import com.example.postapp.service.UserService;

@Controller
@RequestMapping("/empathy")  // アプリ名に合わせて /empathy 以下で処理
public class PostController {
    private final PostService postService;
    private final UserService userService;
    
    public PostController(PostService postService, UserService userService) {
		super();
		this.postService = postService;
		this.userService = userService;
	}

	//Read: 投稿作成画面の表示
    @GetMapping("/create")
    public String showCreateForm(Model model) {
    	model.addAttribute("post", new Post());
    	return "create";//create.htmlに対応している
    }
    
    //Create: 投稿作成
    @PostMapping("/create")
    public String createPost(@RequestParam String content, @AuthenticationPrincipal User user) {
    	postService.createPost(content, user);
    	return "redirect:/empathy/home";//投稿後にタイムライン（ホームへリダイレクト）
    }
    
    //Read: タイムラインの表示
    @GetMapping("/home")
    public String showTimeline(@AuthenticationPrincipal User user, Model model) {
    	//ユーザーの年齢を計算
    	int userAge = postService.calculateAge(postService.convertToDate(user.getDateOfBirth()));
    	
    	//同世代の投稿を取得
    	List<Post> posts = postService.getPostsByAgeGroup(userAge);
    	
    	//世代別の登録数を表示
    	Map<String, Long> ageGroupCounts = userService.getAgeGroupCounts();

    	//投稿をモデルに追加
    	model.addAttribute("ageGroupCounts", ageGroupCounts);   	
    	model.addAttribute("posts", posts);
    	return "home";
    }
    
    //Read: 投稿編集画面の表示
    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model) {
    	Optional<Post> postOptional = postService.getPostById(id);
    	if(postOptional.isEmpty()) {//Optional型のインスタンスに値が存在しないかを確認する
    		return "redirect:/empathy/home";//投稿が存在しない場合はタイムラインに戻る
    	}
    	model.addAttribute("post", postOptional.get());
    	return "update"; //update.htmlに対応
    }
    
    //Update : 投稿を更新
    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, BindingResult result) {
        if (result.hasErrors()) {
            return "update";
        }
        postService.updatePost(id, post.getContent());
        return "redirect:/empathy/home"; // 更新後タイムラインへリダイレクト
    }
    
    //Delete: 投稿削除確認ダイアログの表示（モーダルにするつもり）
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
    	postService.deletePost(id);
    	return "redirect:/empathy/home";// 削除後タイムラインにリダイレクト
    }
    
    //いいねを追加
    @PostMapping("/{id}/like")
    public String addLike(@PathVariable Long id) {
    	postService.addLike(id);
    	return "redirect:/empathy/home";
    }
    
    //頑張ってを追加
    @PostMapping("/{id}/cheer")
    public String addCheer(@PathVariable Long id) {
    	postService.addCheer(id);
    	return "redirect:/empathy/home";
    }
}
