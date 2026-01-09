import { Author } from '../../models/author.model';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthorsService } from '../../services/author.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-authors',
  templateUrl: './authors.component.html',
  standalone:true,
  imports:[FormsModule, CommonModule]
})
export class AuthorsComponent implements OnInit {

  authors: Author[] = [];
  newAuthor: Author = { id: 0, name: '', date_of_birth: '', nationality: '' };

  constructor(private authorsService: AuthorsService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadAuthors();
  }

  loadAuthors(): void {
    this.authorsService.getAllAuthors().subscribe(data =>{ this.authors = data;
      this.cdr.detectChanges();
    });
  }

  addAuthor(): void {
    this.authorsService.addAuthor(this.newAuthor).subscribe(author => {
      this.authors.push(author);
      this.newAuthor = { id: 0, name: '', date_of_birth: '', nationality: '' };
    });
  }

  updateAuthor(author: Author): void {
    this.authorsService.updateAuthor(author.id!, author).subscribe(updated => {
      const index = this.authors.findIndex(a => a.id === updated.id);
      if (index > -1) this.authors[index] = updated;
    });
  }

  deleteAuthor(id: number): void {
    this.authorsService.deleteAuthor(id).subscribe(() => {
      this.authors = this.authors.filter(a => a.id !== id);
    });
  }
}
