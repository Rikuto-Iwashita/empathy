package com.example.postapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.postapp.model.Question;
import com.example.postapp.model.Reply;
import com.example.postapp.model.User;
import com.example.postapp.repository.ReplyRepository;

@Service
public class ReplyService {
	
	private final ReplyRepository replyRepository;
	private final UserService userService;
	
	public ReplyService(ReplyRepository replyRepository, UserService userService) {
		super();
		this.replyRepository = replyRepository;
		this.userService = userService;
	}

	//特定の質問の返信一覧を取得
	public List<Reply> getRepliesByQuestion(Question question) {
		return replyRepository.findByQuestion(question);
	}

	//返信を保存
	public void saveReply(User user, Question question, String content) {
		
		//ログインユーザーの世代を取得
		String userAgeGroup = userService.getAgeGroup(user);
		
		//質問の対象世代がユーザーの世代と一致する場合のみ保存
		if(userAgeGroup.equals(question.getAgeGroup())) {
		Reply reply = new Reply();
		reply.setContent(content);
		reply.setCreatedAt(LocalDateTime.now());
		reply.setQuestion(question);
		reply.setUser(user);
		replyRepository.save(reply);
		} else {
			throw new IllegalArgumentException("この質問には返信できません");
		}
	}
}
