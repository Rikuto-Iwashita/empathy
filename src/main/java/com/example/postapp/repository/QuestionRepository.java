package com.example.postapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postapp.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}