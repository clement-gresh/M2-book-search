package com.searchgutenberg.booksearchengine.repository;


import com.searchgutenberg.booksearchengine.entity.BookGraphData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookGraphRepository extends MongoRepository<BookGraphData,Integer> {

}