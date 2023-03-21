package com.searchgutenberg.booksearchengine.repository;

import com.searchgutenberg.booksearchengine.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book,Integer> {
    public Book findBookById(int id);
    /*
    public void saveBook(Book book);
    public long updateBook(Book book);
    public void deleteBookById(int id);*/
}
