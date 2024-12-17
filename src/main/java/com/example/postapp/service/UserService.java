package com.example.postapp.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.postapp.model.User;
import com.example.postapp.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	//新規ユーザーを登録するメソッド
	public void registerUser(String username, String password, String dateOfBirth) {
		User user = new User();
		user.setUsername(username);//名前を設定
		user.setPassword(passwordEncoder.encode(password));//パスワード設定
		user.setDateOfBirth(dateOfBirth);//生年月日
		userRepository.save(user);
	}
	
	//ログイン認証メソッド
	public Optional<User> loginUser(String username, String password) {
		//ユーザー名でデータベースからユーザーを検索
		Optional<User> userOptional = userRepository.findByUsername(username);
		
		//ユーザー存在かつパスワードが一致したとき
		if(userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword()) ) {
			return userOptional;
		}
		
		//認証失敗時は空のoptionalを返す
		return Optional.empty();
	}
}
