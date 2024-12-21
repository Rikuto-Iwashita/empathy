package com.example.postapp.service;

import org.springframework.stereotype.Service;

import com.example.postapp.model.Reply;
import com.example.postapp.repository.ReplyRepository;

@Service
public class ReplyService {
	
	private final ReplyRepository replyRepository;
	
	public ReplyService(ReplyRepository replyRepository) {
		this.replyRepository = replyRepository;
	}
	
	public Reply saveReply(Reply reply) {
		return replyRepository.save(reply);
	}
}
