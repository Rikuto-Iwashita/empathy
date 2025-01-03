package com.example.postapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.postapp.model.Post;
import com.example.postapp.model.Question;
import com.example.postapp.model.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
	List<Reply> findByQuestion(Question question);
	
	List<Reply> findByPost(Post post);
}
