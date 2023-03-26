package com.searchgutenberg.booksearchengine.repository;

import com.searchgutenberg.booksearchengine.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface BookRepository extends MongoRepository<Book,Integer> {
     Book findBookById(int id);

}
