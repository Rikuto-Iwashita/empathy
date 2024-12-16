package com.example.postapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.postapp.model.Post;
import com.example.postapp.service.PostService;

@Controller
@RequestMapping("/empathy")  // アプリ名に合わせて /empathy 以下で処理
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    
    //Read: 投稿作成画面の表示
    @GetMapping("/create")
    public String showCreateForm(Model model) {
    	model.addAttribute("post", new Post());
    	return "create";//create.htmlに対応している
    }
    
    //Create: 投稿作成
    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, BindingResult result) {
    	if (result.hasErrors()) {
    		return "create";
    	}
    	postService.createPost(post.getContent());
    	return "redirect:/empathy/home";//投稿後にタイムライン（ホームへリダイレクト）
    }
    
    //Read: タイムラインの表示
    @GetMapping("/home")
    public String showTimeline(Model model) {
    	List<Post> posts = postService.getAllPosts();
    	System.out.println("posts" + posts);
        model.addAttribute("posts", posts);
        return "home";  // home.htmlに対応
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
}
