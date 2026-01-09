package com.devops.microservices.author.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devops.microservices.author.model.Author;
import com.devops.microservices.author.repository.AuthorRepository;

@Service
public class AuthorService {
	
    private final AuthorRepository authorRepo;
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    public AuthorService(AuthorRepository authorRepo) {
        this.authorRepo = authorRepo;
    }

    public List<Author> getAllAuthors() {
        try {
            return authorRepo.findAll();
        } catch (Exception e) {
            logger.error("Error fetching authors", e);
            return List.of();
        }
    }

    public Author addAuthor(Author author) {
        try {
            return authorRepo.save(author);
        } catch (Exception e) {
            logger.error("Error adding author: {}", author, e);
            return null;
        }
    }

    public Author updateAuthor(Long id, Author author) {
        try {
            if (!authorRepo.existsById(id)) {
                logger.warn("Author with ID {} not found for update", id);
                return null;
            }
            author.setId(id);
            return authorRepo.save(author);
        } catch (Exception e) {
            logger.error("Error updating author with ID {}: {}", id, author, e);
            return null;
        }
    }

    public boolean deleteAuthor(Long id) {
        try {
            if (!authorRepo.existsById(id)) {
                logger.warn("Author with ID {} not found for deletion", id);
                return false;
            }
            authorRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting author with ID {}", id, e);
            return false;
        }
    }


}
