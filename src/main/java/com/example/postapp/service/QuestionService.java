package com.example.postapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.postapp.model.Question;
import com.example.postapp.model.User;
import com.example.postapp.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionService(QuestionRepository questionRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
    }
    
    //全ての質問を取得
    public List<Question> getAllQuestions() {
    	return questionRepository.findAll();
    }
    
    /* ログインユーザーの同世代の質問と、
     * 他世代から自分の世代向けに送られた質問を取得する。*/
    public List<Question> getFilteredQuestions(User user) {
        // ユーザーの年齢と世代を取得
        int userAge = userService.getUserAge(user);
        String userAgeGroup = userService.getAgeGroupFormAge(userAge);

        // 質問をフィルタリング
        return questionRepository.findAll().stream()
                .filter(question -> {
                    // 質問の対象世代と質問者の世代を取得
                    String questionAgeGroup = question.getAgeGroup();
                    String questionerAgeGroup = userService.getAgeGroup(question.getUser());

                    // nullチェックを追加し、比較を行う
                    boolean isSameAgeGroup = userAgeGroup.equals(questionAgeGroup);
                    boolean isQuestionerSameGroup = (questionerAgeGroup != null) && questionerAgeGroup.equals(userAgeGroup);

                    // 同世代の質問か、他の世代からの自分の世代向けの質問
                    return isSameAgeGroup || isQuestionerSameGroup;
                })
                .collect(Collectors.toList());
    }

    //特定の世代向けの質問を取得
    public List<Question> getQuestionsByTargetAgeGroup(String ageGroup) {
    	return questionRepository.findAll().stream()
    			.filter(question -> ageGroup.equals(question.getAgeGroup()))
    			.collect(Collectors.toList());
    }
    
    //自分の世代に向けられた質問を取得
    public List<Question> getQuestionsToSpecificAgeGroup(User user) {
    	//ユーザーと世代を取得
    	int userAge = userService.getUserAge(user);
    	String userAgeGroup = userService.getAgeGroupFormAge(userAge);
    	
    	//質問をフィルタリング
    	return questionRepository.findAll().stream()
    			.filter(question -> {
    				//質問の対象世代と質問者の世代を取得
    				String questionAgeGroup = question.getAgeGroup();
    				String questionerAgeGroup = userService.getAgeGroup(question.getUser());
    				
    				//質問者の世代が異なるかつ質問の対象世代がログインユーザーの世代と一致する
    				boolean isFromOtherGeneration = !userAgeGroup.equals(questionerAgeGroup);//質問者の世代が違う
    				boolean isToUserGeneration = userAgeGroup.equals(questionAgeGroup);//質問がログインユーザーの世代向け
    				
    				//他の世代から自分の世代への質問のみを表示
    				return isFromOtherGeneration && isToUserGeneration;
    			})
    			.collect(Collectors.toList());
    }
    
    //ログインしているユーザーから、その世代へ向けた質問のみ取得
    public List<Question> getQuestionsFromAndToSameGeneration(User user) {
        // ユーザーの年齢と世代を取得
        int userAge = userService.getUserAge(user);
        String userAgeGroup = userService.getAgeGroupFormAge(userAge);

        // 質問をフィルタリング
        return questionRepository.findAll().stream()
                .filter(question -> {
                    User questioner = question.getUser();
                    if (questioner == null) {
                        return false; // 質問者が不明な場合はスキップ
                    }
                    String questionerAgeGroup = userService.getAgeGroup(questioner);
                    String targetAgeGroup = question.getAgeGroup();

                    // 質問者の世代と、質問の対象世代が両方ともログインユーザーの世代と一致する場合のみ表示
                    return userAgeGroup.equals(questionerAgeGroup) && userAgeGroup.equals(targetAgeGroup);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 新しい質問を作成する
     * @param content 質問内容
     * @param ageGroup 対象世代
     * @param user 質問者
     */
    public void createQuestion(String title, String content, String ageGroup, User user) {
    	Question question = new Question();
    	question.setTitle(title);
    	question.setContent(content);
    	question.setAgeGroup(ageGroup);
    	question.setCreatedAt(LocalDateTime.now());
    	question.setUser(user);
    	questionRepository.save(question);
    }
    
    //質問をIDで取得する
    public Question getQuestionById(Long id) {
    	return questionRepository.findById(id).orElse(null);
    }
}
