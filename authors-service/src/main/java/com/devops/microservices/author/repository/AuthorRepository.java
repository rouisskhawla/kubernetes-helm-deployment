package com.devops.microservices.author.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devops.microservices.author.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {}
