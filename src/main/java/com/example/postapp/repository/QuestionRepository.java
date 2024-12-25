package com.example.postapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.postapp.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	
	//同世代の質問を取得
	List<Question> findByAgeGroup(String ageGroup);
	
	//ログインユーザーに向けた質問を取得
	@Query("SELECT q FROM Question q WHERE q.ageGroup = :targetAgeGroup")
	List<Question> findByTargetAgeGroup(@Param("targetAgeGroup") String targetAgeGroup);
}