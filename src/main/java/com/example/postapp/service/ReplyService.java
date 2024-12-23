package com.example.postapp.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.postapp.model.Question;
import com.example.postapp.model.Reply;
import com.example.postapp.model.User;
import com.example.postapp.repository.ReplyRepository;

@Service
public class ReplyService {
	
	private final ReplyRepository replyRepository;
	
	public ReplyService(ReplyRepository replyRepository) {
		this.replyRepository = replyRepository;
	}
	
	//返信を保存
	public void saveReply(String content, Question question, User user) {
		Reply reply = new Reply();
		reply.setContent(content);
		reply.setCreatedAt(LocalDateTime.now());
		reply.setQuestion(question);
		reply.setUser(user);
		replyRepository.save(reply);
	}
}
