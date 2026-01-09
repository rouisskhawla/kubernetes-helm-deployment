package com.devops.microservices.author.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devops.microservices.author.model.Author;
import com.devops.microservices.author.model.StandardResponses;
import com.devops.microservices.author.service.AuthorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Authors", description = "Endpoints for Authors API")
@StandardResponses
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
	@Operation(summary = "Get Authors", description = "List all the Authors saved to database")
	public List<Author> getAllAuthors() {
		return authorService.getAllAuthors();
	}

	@PostMapping
	@Operation(summary = "Add Authors", description = "Add a new Author to database")
	public Author addAuthor(@RequestBody Author author) {
		return authorService.addAuthor(author);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update Authors", description = "Update Author by ID")
	public ResponseEntity<Map<String, Object>> updateAuthor(@PathVariable Long id, @RequestBody Author author) {
		Author updatedAuthor = authorService.updateAuthor(id, author);
		Map<String, Object> response = new HashMap<>();

		if (updatedAuthor == null) {
			response.put("message", "Author not found or could not be updated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		response.put("message", "Author updated successfully");
		response.put("updatedAuthor", updatedAuthor);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete Authors", description = "Delete Author by ID")
	public ResponseEntity<Map<String, String>> deleteAuthor(@PathVariable Long id) {
		boolean deleted = authorService.deleteAuthor(id);
		Map<String, String> response = new HashMap<>();

		if (!deleted) {
			response.put("message", "Author not found or could not be deleted");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		response.put("message", "Author deleted successfully");
		return ResponseEntity.ok(response);
	}
}
