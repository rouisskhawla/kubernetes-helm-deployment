import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { BooksService } from '../../services/book.service';
import { Book } from '../../models/book.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-books',
  templateUrl: './books.component.html',
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class BooksComponent implements OnInit {

  books: Book[] = [];
  newBook: Book = { id: 0, title: '', author: '', year: 0 };

  constructor(private booksService: BooksService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.booksService.getAllBooks().subscribe(data => {
      this.books = data;
      this.cdr.detectChanges();
    });
  }

  addBook(): void {
    this.booksService.addBook(this.newBook).subscribe(book => {
      this.books.push(book);
      this.newBook = { id: 0, title: '', author: '', year: 0 };
    });
  }

  updateBook(book: Book): void {
    this.booksService.updateBook(book.id!, book).subscribe(updated => {
      const index = this.books.findIndex(b => b.id === updated.id);
      if (index > -1) this.books[index] = updated;
    });
  }

  deleteBook(id: number): void {
    this.booksService.deleteBook(id).subscribe(() => {
      this.books = this.books.filter(b => b.id !== id);
    });
  }
}
