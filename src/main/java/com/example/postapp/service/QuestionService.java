package com.example.postapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.postapp.model.Question;
import com.example.postapp.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
    
    //全ての質問を取得
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    //質問IDで質問を検索
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }
    
    //質問を作成
    public void createQuestion(Question question) {
        questionRepository.save(question);
    }
}
