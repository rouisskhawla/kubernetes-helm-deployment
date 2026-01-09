package com.devops.microservices.books.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devops.microservices.books.model.Book;
import com.devops.microservices.books.model.StandardResponses;
import com.devops.microservices.books.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Endpoints for Books API")
@StandardResponses
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	@Operation(summary = "Get Books", description = "List all the books saved to database")
	public List<Book> getAllBooks() {
		return bookService.getAllBooks();
	}

	@PostMapping
	@Operation(summary = "Add Books", description = "Add a new book to database")
	public Book addBook(@RequestBody Book book) {
		return bookService.addBook(book);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update Books", description = "Update book by ID")
	public ResponseEntity<Map<String, Object>> updateBook(@PathVariable Long id, @RequestBody Book book) {
		Book updatedBook = bookService.updateBook(id, book);
		Map<String, Object> response = new HashMap<>();

		if (updatedBook == null) {
			response.put("message", "Book not found or could not be updated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		response.put("message", "Book updated successfully");
		response.put("updatedBook", updatedBook);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete Books", description = "Delete book by ID")
	public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
		boolean deleted = bookService.deleteBook(id);
		Map<String, String> response = new HashMap<>();

		if (!deleted) {
			response.put("message", "Book not found or could not be deleted");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		response.put("message", "Book deleted successfully");
		return ResponseEntity.ok(response);
	}
}
