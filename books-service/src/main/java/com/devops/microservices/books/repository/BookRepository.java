package com.devops.microservices.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devops.microservices.books.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {}
