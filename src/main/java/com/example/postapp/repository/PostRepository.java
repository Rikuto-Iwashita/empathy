package com.example.postapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postapp.model.Post;

public interface PostRepository extends JpaRepository<Post, Long>{

}
