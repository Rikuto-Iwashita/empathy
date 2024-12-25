package com.example.postapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.postapp.model.Post;
import com.example.postapp.model.User;
import com.example.postapp.repository.PostRepository;

@Service
public class PostService {
	private final PostRepository postRepository;
	
	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
	}
	
	//全投稿取得
	public List<Post> getAllPosts() {
		return postRepository.findAll();
	}
	
	//新規投稿を作成
	public void createPost(String content, User user) {
		Post post = new Post();
		post.setContent(content);
		post.setCreatedAt(LocalDateTime.now());
		post.setUser(user);
		postRepository.save(post); //リポジトリに保存
	}
	
	//投稿をIDで取得
	public Optional<Post> getPostById(Long id) {
		return postRepository.findById(id);//IDをキーに検索
	}
	
	//投稿を取得し、編集、削除権限を確認
	public Post getPostForEditOrDelete(Long id, User loggedInUser) throws IllegalAccessException{
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalAccessException("投稿が見つかりません"));
		
		//投稿したユーザーとログインユーザーを比較
		if(!post.getUser().getId().equals(loggedInUser.getId()) ) {
			throw new IllegalAccessException("この投稿を編集または削除する権限がありません");
		}
		
		return post;
	}
	
	//投稿を削除
	public void deletePost(Long id, User loggedInUser) throws IllegalAccessException{
		Post post = getPostForEditOrDelete(id, loggedInUser);
		postRepository.delete(post);
	}
	
	//投稿を更新
	public void updatePost(Long id, String content, User loggedInUser) throws IllegalAccessException{
		Post post = getPostForEditOrDelete(id, loggedInUser);
		post.setContent(content);
		postRepository.save(post);
	}
	
	//投稿にいいねを追加
	public boolean addLike(Long id) {
		Optional<Post> postOptional =postRepository.findById(id);
		if(postOptional.isPresent()) {
			Post post = postOptional.get();
			post.setLikeCount(post.getLikeCount() + 1);//いいね数を増やす
			postRepository.save(post);
			return true;
		}
		return false;
	}
	
	//投稿に頑張ってを追加
	public boolean addCheer(Long id) {
		Optional<Post> postOptional = postRepository.findById(id);
		if(postOptional.isPresent()) {
			Post post = postOptional.get();
			post.setCheerCount(post.getCheerCount() + 1);//頑張ってを増やす。
			postRepository.save(post);
			return true;
		}
		return false;
	}
	
    // 世代別投稿を取得
    public List<Post> getPostsByAgeGroup(int userAge) {
        return postRepository.findAll().stream()
                .filter(post -> {
                	//Userの生年月日をLocalDateに変換
                	LocalDate dateOfBirth = convertToDate(post.getUser().getDateOfBirth());
                	if(dateOfBirth == null) {
                		return false;//生年月日が無効の場合はフィルタから除外
                	}
                    int postUserAge = calculateAge(dateOfBirth);
                    return isSameAgeGroup(userAge, postUserAge);
                })
                .collect(Collectors.toList());
    }
    
    //StringをLocalDateに変換するメソッド。世代別投稿のメソッドを成り立たせるために必要
	public LocalDate convertToDate(String dateOfBirth) {
		try {
			return LocalDate.parse(dateOfBirth);
		} catch (Exception e) {
			// 無効なフォーマットの場合はnull
			return null;
		}
	}
    
    // ユーザーの年齢を計算
    public int calculateAge(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(dateOfBirth, currentDate).getYears();
    }
	
	//同世代かどうかを判定
	private boolean isSameAgeGroup(int age1, int age2) {
		return getAgeGroup(age1).equals(getAgeGroup(age2));
	}
	
	//年齢を基に世代を判定
	private String getAgeGroup(int age) {
		if(age >= 13 && age <= 18) {
			return "13-18";
		}else if(age >= 19 && age <= 24) {
			return "19-24";
		}else if(age >= 25 && age <= 29) {
			return "25-29";
		}else if(age >= 30 && age <= 39) {
			return "30-39";
		}else {
			return "40+";
		}
	}
}
