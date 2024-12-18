package com.example.postapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
	
	//投稿を更新
	public boolean updatePost(Long id, String content) {
		Optional<Post> existingPost = postRepository.findById(id);
		if(existingPost.isPresent()) {//投稿が存在（IDが見つかる場合）以下を実行
			Post post = existingPost.get();
			post.setContent(content);//コンテンツ更新
			post.setCreatedAt(LocalDateTime.now());//日時も更新
			postRepository.save(post);//更新内容を保存
			return true;
		}
		return false;//IDが見つからない場合
	}
	
	//投稿を削除
	public boolean deletePost(Long id) {
		if(postRepository.existsById(id)) {//IDが存在するか確認
			postRepository.deleteById(id);//存在するならば削除する。
			return true;
		}
		return false;//IDが見つからない場合
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
}
