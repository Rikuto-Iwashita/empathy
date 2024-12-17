package com.example.postapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.postapp.model.User;
import com.example.postapp.service.UserService;

@Controller
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	//新規登録フォームの表示
	@GetMapping("/signup")
	public String showSignupForm(Model model) {
		model.addAttribute("user", new User());//フォーム用に空のUserオブジェクトを追加
		return "signup";
	}
	
	//新規登録処理
	@PostMapping("/signup")
	public String registerUser(@ModelAttribute @Validated User user, BindingResult result) {
		if(result.hasErrors()) {
			return "signup";//入力エラーの場合、再表示
		}
		userService.registerUser(user.getUsername(), user.getPassword(), user.getDateOfBirth());
		return "redirect:/login";//登録成功後ログインページへリダイレクト
	}
	
	//ログインフォームの表示
	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}
	
	//ログイン処理
	@PostMapping("/login")
	public String loginUser(@ModelAttribute User user, Model model) {
		//メソッドの戻り値はOptional<User>という事が分かっているので型推論を使った（コードを短くするため）
		var userOptional = userService.loginUser(user.getUsername(), user.getPassword());
		if(userOptional.isPresent()) {
			model.addAttribute("currentUser", userOptional.get());//ログイン成功時に現在のユーザーをモデルに追加
			return "redirect:/home";//ログインしたらホーム画面に移動
		}
		model.addAttribute("error","ユーザー名またはパスワードが間違っています。");//エラーメッセージ
		return "login";//失敗したらログインフォームを再表示
	}
	
	//ログアウト処理
	@GetMapping("/logout")
	public String logoutUser() {
		//データをクリアしたりする処理が必要ならここに書く
		return "redirect:/login";//ログアウトしたらログイン画面に戻る
	}
	
}
