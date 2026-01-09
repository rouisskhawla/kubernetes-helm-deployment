package com.devops.microservices.books.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.devops.microservices.books.model.Book;
import com.devops.microservices.books.repository.BookRepository;

@Service
public class BookService {
	
	private final BookRepository bookRepo;
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getAllBooks() {
        try {
            return bookRepo.findAll();
        } catch (Exception e) {
            logger.error("Error fetching books", e);
            return List.of();
        }
    }

    public Book addBook(Book book) {
        try {
            return bookRepo.save(book);
        } catch (Exception e) {
            logger.error("Error adding book: {}", book, e);
            return null;
        }
    }

    public Book updateBook(Long id, Book book) {
        try {
            if (!bookRepo.existsById(id)) {
                logger.warn("Book with ID {} not found for update", id);
                return null;
            }
            book.setId(id);
            return bookRepo.save(book);
        } catch (Exception e) {
            logger.error("Error updating book with ID {}: {}", id, book, e);
            return null;
        }
    }

    public boolean deleteBook(Long id) {
        try {
            if (!bookRepo.existsById(id)) {
                logger.warn("Book with ID {} not found for deletion", id);
                return false;
            }
            bookRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting book with ID {}", id, e);
            return false;
        }
    }

}
