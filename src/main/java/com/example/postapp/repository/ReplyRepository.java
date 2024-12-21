package com.example.postapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postapp.model.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

}
